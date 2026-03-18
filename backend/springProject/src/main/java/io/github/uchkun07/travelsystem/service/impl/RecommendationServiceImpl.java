package io.github.uchkun07.travelsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.uchkun07.travelsystem.config.RecommendAlgorithmProperties;
import io.github.uchkun07.travelsystem.dto.AttractionCardResponse;
import io.github.uchkun07.travelsystem.dto.PageResponse;
import io.github.uchkun07.travelsystem.dto.RecommendHomeRequest;
import io.github.uchkun07.travelsystem.dto.RecommendHomeResponse;
import io.github.uchkun07.travelsystem.dto.RecommendTrackRequest;
import io.github.uchkun07.travelsystem.dto.RecommendTypeBehaviorStat;
import io.github.uchkun07.travelsystem.entity.Attraction;
import io.github.uchkun07.travelsystem.entity.AttractionType;
import io.github.uchkun07.travelsystem.entity.City;
import io.github.uchkun07.travelsystem.entity.UserBrowseRecord;
import io.github.uchkun07.travelsystem.entity.UserPreference;
import io.github.uchkun07.travelsystem.mapper.AttractionMapper;
import io.github.uchkun07.travelsystem.mapper.AttractionTypeMapper;
import io.github.uchkun07.travelsystem.mapper.CityMapper;
import io.github.uchkun07.travelsystem.mapper.UserBrowseRecordMapper;
import io.github.uchkun07.travelsystem.mapper.UserPreferenceMapper;
import io.github.uchkun07.travelsystem.service.IRecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements IRecommendationService {

    private static final int CANDIDATE_SIZE = 500;

    private final AttractionMapper attractionMapper;
    private final AttractionTypeMapper attractionTypeMapper;
    private final CityMapper cityMapper;
    private final UserPreferenceMapper userPreferenceMapper;
    private final UserBrowseRecordMapper userBrowseRecordMapper;
    private final RecommendAlgorithmProperties recommendProps;
    private final ObjectMapper objectMapper;

    @Override
    public RecommendHomeResponse getHomeRecommendations(Long userId, RecommendHomeRequest request) {
        long pageNum = normalizePageNum(request == null ? null : request.getPageNum());
        long pageSize = normalizePageSize(request == null ? null : request.getPageSize());

        List<Attraction> candidates = queryCandidates();
        String requestId = UUID.randomUUID().toString().replace("-", "");
        if (candidates.isEmpty()) {
            return RecommendHomeResponse.builder()
                    .requestId(requestId)
                    .recVersion(recommendProps.getRecVersion())
                    .behaviorEnabled(false)
                    .page(PageResponse.<AttractionCardResponse>builder()
                            .records(List.of())
                            .total(0L)
                            .pageNum(pageNum)
                            .pageSize(pageSize)
                            .totalPages(0L)
                            .hasPrevious(false)
                            .hasNext(false)
                            .build())
                    .build();
        }

        UserPreference preference = loadUserPreference(userId);
        long behaviorEventCount = 0L;
        List<RecommendTypeBehaviorStat> behaviorStats = List.of();
        if (userId != null) {
            Integer window = recommendProps.getBehaviorWindowDays();
            Long cnt = userBrowseRecordMapper.countRecentBehaviorEvents(userId, window);
            behaviorEventCount = cnt == null ? 0L : cnt;
            behaviorStats = userBrowseRecordMapper.aggregateUserBehaviorByType(userId, window);
        }

        PythonRankResult rankResult = callPythonRank(candidates, preference, behaviorStats, behaviorEventCount);

        List<Attraction> orderedAttractions = reorderByIds(candidates, rankResult.orderedIds());

        Map<Integer, String> typeNameMap = loadTypeNameMap(orderedAttractions);
        Map<Integer, String> cityNameMap = loadCityNameMap(orderedAttractions);

        long total = orderedAttractions.size();
        long totalPages = (total + pageSize - 1) / pageSize;
        int from = (int) Math.min((pageNum - 1) * pageSize, total);
        int to = (int) Math.min(from + pageSize, total);
        List<Attraction> pageItems = orderedAttractions.subList(from, to);

        List<AttractionCardResponse> records = pageItems.stream()
                .map(item -> toCard(item, typeNameMap, cityNameMap))
                .toList();

        PageResponse<AttractionCardResponse> page = PageResponse.<AttractionCardResponse>builder()
                .records(records)
                .total(total)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .totalPages(totalPages)
                .hasPrevious(pageNum > 1)
                .hasNext(pageNum < totalPages)
                .build();

        return RecommendHomeResponse.builder()
                .requestId(requestId)
                .recVersion(recommendProps.getRecVersion())
                .behaviorEnabled(rankResult.behaviorEnabled())
                .page(page)
                .build();
    }

    @Override
    public void track(Long userId, RecommendTrackRequest request, String userAgent) {
        if (userId == null) {
            // 游客不上报到用户行为表，避免脏数据
            return;
        }

        String eventType = normalizeEventType(request.getEventType());
        int staySeconds = "stay".equals(eventType) ? Math.max(defaultInt(request.getStaySeconds()), 0) : 0;

        String requestIdShort = safeShort(request.getRequestId(), 8);
        String sourcePage = safeShort(request.getSourcePage(), 12);
        String recVersion = safeShort(request.getRecVersion(), 12);
        String ua = safeShort(userAgent, 12);
        Integer pos = request.getPosition();

        // 兼容当前表结构（device_info varchar(100)），使用短KV字符串
        String deviceInfo = "et=" + eventType +
                ";rid=" + requestIdShort +
                ";pos=" + (pos == null ? 0 : Math.max(pos, 0)) +
                ";pg=" + sourcePage +
                ";rv=" + recVersion +
                ";ua=" + ua;

        if (deviceInfo.length() > 100) {
            deviceInfo = deviceInfo.substring(0, 100);
        }

        UserBrowseRecord record = UserBrowseRecord.builder()
                .userId(userId)
                .attractionId(request.getAttractionId())
                .browseDuration(staySeconds)
                .browseTime(LocalDateTime.now())
                .deviceInfo(deviceInfo)
                .build();

        userBrowseRecordMapper.insert(record);
    }

    private PythonRankResult callPythonRank(List<Attraction> candidates,
                                            UserPreference preference,
                                            List<RecommendTypeBehaviorStat> behaviorStats,
                                            long behaviorEventCount) {
        try {
            Map<String, Object> input = new LinkedHashMap<>();
            input.put("candidates", buildCandidatePayload(candidates));
            input.put("preference", buildPreferencePayload(preference));
            input.put("behaviorByType", buildBehaviorPayload(behaviorStats));
            input.put("behaviorEventCount", behaviorEventCount);
            input.put("params", buildAlgoParams());

            String inputJson = objectMapper.writeValueAsString(input);
            String scriptPath = resolveScriptPath();

            ProcessBuilder pb = new ProcessBuilder("python", scriptPath);
            pb.redirectErrorStream(false);
            Process process = pb.start();

            try (OutputStream os = process.getOutputStream()) {
                os.write(inputJson.getBytes(StandardCharsets.UTF_8));
            }

            String stdout;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                stdout = sb.toString();
            }

            try (BufferedReader errReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
                String errLine;
                while ((errLine = errReader.readLine()) != null) {
                    log.debug("[Python Recommend] {}", errLine);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0 || !StringUtils.hasText(stdout)) {
                log.warn("推荐算法 Python 脚本执行异常，exitCode={}，降级使用热门排序", exitCode);
                return fallbackRank(behaviorEventCount);
            }

            JsonNode root = objectMapper.readTree(stdout);
            List<Long> orderedIds = new ArrayList<>();
            JsonNode arr = root.path("orderedIds");
            if (arr.isArray()) {
                for (JsonNode n : arr) {
                    if (n.isNumber()) {
                        orderedIds.add(n.asLong());
                    }
                }
            }
            boolean behaviorEnabled = root.path("behaviorEnabled").asBoolean(false);
            return new PythonRankResult(orderedIds, behaviorEnabled);
        } catch (Exception e) {
            log.warn("调用 Python 推荐算法失败，降级使用热门排序: {}", e.getMessage());
            return fallbackRank(behaviorEventCount);
        }
    }

    private PythonRankResult fallbackRank(long behaviorEventCount) {
        boolean behaviorEnabled = behaviorEventCount >= recommendProps.getBehaviorSwitchThreshold();
        return new PythonRankResult(List.of(), behaviorEnabled);
    }

    private List<Map<String, Object>> buildCandidatePayload(List<Attraction> candidates) {
        List<Map<String, Object>> list = new ArrayList<>(candidates.size());
        for (Attraction a : candidates) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("attractionId", a.getAttractionId());
            m.put("typeId", a.getTypeId());
            m.put("browseCount", defaultInt(a.getBrowseCount()));
            m.put("favoriteCount", defaultInt(a.getFavoriteCount()));
            m.put("averageRating", a.getAverageRating() == null ? 0.0 : a.getAverageRating().doubleValue());
            m.put("ticketPrice", a.getTicketPrice() == null ? 0.0 : a.getTicketPrice().doubleValue());
            m.put("bestSeason", a.getBestSeason());
            list.add(m);
        }
        return list;
    }

    private Map<String, Object> buildPreferencePayload(UserPreference preference) {
        if (preference == null) {
            return null;
        }
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("preferAttractionTypeId", preference.getPreferAttractionTypeId());
        p.put("budgetFloor", preference.getBudgetFloor());
        p.put("budgetRange", preference.getBudgetRange());
        p.put("preferSeason", preference.getPreferSeason());
        return p;
    }

    private Map<String, Object> buildBehaviorPayload(List<RecommendTypeBehaviorStat> behaviorStats) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (RecommendTypeBehaviorStat stat : behaviorStats) {
            if (stat.getTypeId() == null) {
                continue;
            }
            Map<String, Object> value = new LinkedHashMap<>();
            value.put("clickCount", defaultInt(stat.getClickCount()));
            value.put("staySeconds", defaultInt(stat.getStaySeconds()));
            map.put(String.valueOf(stat.getTypeId()), value);
        }
        return map;
    }

    private Map<String, Object> buildAlgoParams() {
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("behaviorSwitchThreshold", recommendProps.getBehaviorSwitchThreshold());
        p.put("coldPreferenceWeight", recommendProps.getColdPreferenceWeight());
        p.put("coldHotWeight", recommendProps.getColdHotWeight());
        p.put("matureProfileWeight", recommendProps.getMatureProfileWeight());
        p.put("matureHotWeight", recommendProps.getMatureHotWeight());
        p.put("profileExplicitWeight", recommendProps.getProfileExplicitWeight());
        p.put("profileImplicitWeight", recommendProps.getProfileImplicitWeight());
        p.put("behaviorClickWeight", recommendProps.getBehaviorClickWeight());
        p.put("behaviorStayWeight", recommendProps.getBehaviorStayWeight());
        return p;
    }

    private String resolveScriptPath() {
        String configured = recommendProps.getScriptPath();
        if (StringUtils.hasText(configured)) {
            return configured;
        }
        return Paths.get(System.getProperty("user.dir"))
                .getParent().resolve("python/recommendationAlgorithm.py")
                .toAbsolutePath().toString();
    }

    private List<Attraction> reorderByIds(List<Attraction> candidates, List<Long> orderedIds) {
        if (orderedIds == null || orderedIds.isEmpty()) {
            return candidates;
        }

        Map<Long, Attraction> byId = candidates.stream()
                .collect(Collectors.toMap(Attraction::getAttractionId, a -> a, (a, b) -> a, LinkedHashMap::new));

        List<Attraction> result = new ArrayList<>(candidates.size());
        Set<Long> used = new java.util.HashSet<>();

        for (Long id : orderedIds) {
            Attraction a = byId.get(id);
            if (a != null && used.add(id)) {
                result.add(a);
            }
        }

        for (Attraction a : candidates) {
            if (used.add(a.getAttractionId())) {
                result.add(a);
            }
        }

        return result;
    }

    private List<Attraction> queryCandidates() {
        Page<Attraction> page = new Page<>(1, CANDIDATE_SIZE);
        LambdaQueryWrapper<Attraction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attraction::getAuditStatus, 2)
                .eq(Attraction::getStatus, 1)
                .orderByDesc(Attraction::getPopularity)
                .orderByDesc(Attraction::getBrowseCount)
                .orderByDesc(Attraction::getFavoriteCount)
                .orderByDesc(Attraction::getAverageRating);
        return attractionMapper.selectPage(page, wrapper).getRecords();
    }

    private UserPreference loadUserPreference(Long userId) {
        if (userId == null) {
            return null;
        }
        LambdaQueryWrapper<UserPreference> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPreference::getUserId, userId).last("LIMIT 1");
        return userPreferenceMapper.selectOne(wrapper);
    }

    private Map<Integer, String> loadTypeNameMap(List<Attraction> attractions) {
        Set<Integer> ids = attractions.stream().map(Attraction::getTypeId).filter(i -> i != null).collect(Collectors.toSet());
        if (ids.isEmpty()) {
            return Map.of();
        }
        LambdaQueryWrapper<AttractionType> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(AttractionType::getTypeId, ids);
        return attractionTypeMapper.selectList(wrapper).stream()
                .collect(Collectors.toMap(AttractionType::getTypeId, AttractionType::getTypeName, (a, b) -> a));
    }

    private Map<Integer, String> loadCityNameMap(List<Attraction> attractions) {
        Set<Integer> ids = attractions.stream().map(Attraction::getCityId).filter(i -> i != null).collect(Collectors.toSet());
        if (ids.isEmpty()) {
            return Map.of();
        }
        LambdaQueryWrapper<City> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(City::getCityId, ids);
        return cityMapper.selectList(wrapper).stream()
                .collect(Collectors.toMap(City::getCityId, City::getCityName, (a, b) -> a));
    }

    private AttractionCardResponse toCard(Attraction attraction,
                                          Map<Integer, String> typeNameMap,
                                          Map<Integer, String> cityNameMap) {
        String description = StringUtils.hasText(attraction.getSubtitle()) ? attraction.getSubtitle() : attraction.getDescription();
        if (!StringUtils.hasText(description)) {
            description = "暂无描述";
        }

        return AttractionCardResponse.builder()
                .attractionId(attraction.getAttractionId())
                .name(attraction.getName())
                .description(description)
                .type(typeNameMap.getOrDefault(attraction.getTypeId(), "未知类型"))
                .location(cityNameMap.getOrDefault(attraction.getCityId(), "未知城市"))
                .imageUrl(attraction.getMainImageUrl())
                .averageRating(attraction.getAverageRating())
                .viewCount(attraction.getBrowseCount())
                .popularity(attraction.getPopularity())
                .ticketPrice(attraction.getTicketPrice())
                .build();
    }

    private long normalizePageNum(Long pageNum) {
        return pageNum == null || pageNum < 1 ? 1 : pageNum;
    }

    private long normalizePageSize(Long pageSize) {
        if (pageSize == null || pageSize < 1) {
            return 20;
        }
        return Math.min(pageSize, 100);
    }

    private String normalizeEventType(String raw) {
        if (!StringUtils.hasText(raw)) {
            throw new IllegalArgumentException("eventType 不能为空");
        }
        String eventType = raw.trim().toLowerCase();
        Set<String> allowed = Set.of("impression", "click", "stay");
        if (!allowed.contains(eventType)) {
            throw new IllegalArgumentException("eventType 非法，仅支持 impression/click/stay");
        }
        return eventType;
    }

    private int defaultInt(Integer v) {
        return v == null ? 0 : v;
    }

    private String safeShort(String value, int maxLen) {
        if (!StringUtils.hasText(value)) {
            return "na";
        }
        String v = value.replace(";", "_").replace("=", "_").trim();
        return v.length() <= maxLen ? v : v.substring(0, maxLen);
    }

    private record PythonRankResult(List<Long> orderedIds, boolean behaviorEnabled) {
    }
}
