package io.github.uchkun07.travelsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements IRecommendationService {

    private static final int CANDIDATE_SIZE = 500;
    private static final int PREFERRED_TYPE_APPEND_SIZE = 200;

    private final AttractionMapper attractionMapper;
    private final AttractionTypeMapper attractionTypeMapper;
    private final CityMapper cityMapper;
    private final UserPreferenceMapper userPreferenceMapper;
    private final UserBrowseRecordMapper userBrowseRecordMapper;
    private final RecommendAlgorithmProperties recommendProps;

    @Override
    public RecommendHomeResponse getHomeRecommendations(Long userId, RecommendHomeRequest request) {
        long pageNum = normalizePageNum(request == null ? null : request.getPageNum());
        long pageSize = normalizePageSize(request == null ? null : request.getPageSize());

        UserPreference preference = loadUserPreference(userId);
        List<Attraction> candidates = queryCandidates(preference);
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

        long behaviorEventCount = 0L;
        List<RecommendTypeBehaviorStat> behaviorStats = List.of();
        if (userId != null) {
            Integer window = recommendProps.getBehaviorWindowDays();
            Long cnt = userBrowseRecordMapper.countRecentBehaviorEvents(userId, window);
            behaviorEventCount = cnt == null ? 0L : cnt;
            behaviorStats = userBrowseRecordMapper.aggregateUserBehaviorByType(userId, window);
        }

        RankResult rankResult = rankInJava(candidates, preference, behaviorStats, behaviorEventCount);

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
        // 仅保留“详情停留”行为，首页曝光/点击不再入库。
        if (!"stay".equals(eventType)) {
            return;
        }

        int staySeconds = Math.max(defaultInt(request.getStaySeconds()), 0);
        if (staySeconds < 2) {
            return;
        }

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
            // 记录用户进入详情页的大致时间点，便于后续行为分析。
            .browseTime(LocalDateTime.now().minusSeconds(staySeconds))
                .deviceInfo(deviceInfo)
                .build();

        userBrowseRecordMapper.insert(record);
    }

    private RankResult fallbackRank(long behaviorEventCount) {
        boolean behaviorEnabled = behaviorEventCount >= recommendProps.getBehaviorSwitchThreshold();
        return new RankResult(List.of(), behaviorEnabled);
    }

    private RankResult rankInJava(List<Attraction> candidates,
                                  UserPreference preference,
                                  List<RecommendTypeBehaviorStat> behaviorStats,
                                  long behaviorEventCount) {
        try {
            if (candidates.isEmpty()) {
                return new RankResult(List.of(), false);
            }

            boolean behaviorEnabled = behaviorEventCount >= recommendProps.getBehaviorSwitchThreshold();
            Integer preferTypeId = preference == null ? null : preference.getPreferAttractionTypeId();

            int maxBrowse = candidates.stream().map(Attraction::getBrowseCount).filter(Objects::nonNull).max(Integer::compareTo).orElse(1);
            int maxFavorite = candidates.stream().map(Attraction::getFavoriteCount).filter(Objects::nonNull).max(Integer::compareTo).orElse(1);
            double maxRating = candidates.stream()
                    .map(Attraction::getAverageRating)
                    .filter(Objects::nonNull)
                    .mapToDouble(Number::doubleValue)
                    .max()
                    .orElse(5.0);

            Map<Integer, RecommendTypeBehaviorStat> behaviorByType = behaviorStats.stream()
                    .filter(s -> s.getTypeId() != null)
                    .collect(Collectors.toMap(RecommendTypeBehaviorStat::getTypeId, s -> s, (a, b) -> a));

            int maxClick = behaviorByType.values().stream().map(RecommendTypeBehaviorStat::getClickCount)
                    .filter(Objects::nonNull).max(Integer::compareTo).orElse(1);
            int maxStay = behaviorByType.values().stream().map(RecommendTypeBehaviorStat::getStaySeconds)
                    .filter(Objects::nonNull).max(Integer::compareTo).orElse(1);

            List<ScoredAttraction> scored = new ArrayList<>(candidates.size());
            for (Attraction item : candidates) {
                double hs = hotScore(item, maxBrowse, maxFavorite, maxRating);
                double es = explicitScore(item, preference);
                double ims = implicitScore(item, behaviorByType, maxClick, maxStay);

                double total;
                if (preference == null) {
                    total = hs;
                } else if (behaviorEnabled) {
                    double profile = recommendProps.getProfileExplicitWeight() * es
                            + recommendProps.getProfileImplicitWeight() * ims;
                    total = recommendProps.getMatureProfileWeight() * profile
                            + recommendProps.getMatureHotWeight() * hs;
                } else {
                    total = recommendProps.getColdPreferenceWeight() * es
                            + recommendProps.getColdHotWeight() * hs;
                }

                boolean typeMatch = preferTypeId != null && preferTypeId.equals(item.getTypeId());
                scored.add(new ScoredAttraction(item, round(total), typeMatch));
            }

            Comparator<ScoredAttraction> byScoreDesc = Comparator.comparing(ScoredAttraction::score).reversed();
            if (preferTypeId != null) {
                scored.sort(Comparator.comparing(ScoredAttraction::typeMatch).reversed().thenComparing(byScoreDesc));
            } else {
                scored.sort(byScoreDesc);
            }

            List<ScoredAttraction> diversified = diversify(scored, 3);
            List<Long> orderedIds = diversified.stream()
                    .map(s -> s.attraction().getAttractionId())
                    .filter(Objects::nonNull)
                    .toList();
            return new RankResult(orderedIds, behaviorEnabled);
        } catch (Exception ex) {
            log.warn("Java 推荐排序异常，降级热门排序: {}", ex.getMessage());
            return fallbackRank(behaviorEventCount);
        }
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

    private List<Attraction> queryCandidates(UserPreference preference) {
        Page<Attraction> page = new Page<>(1, CANDIDATE_SIZE);
        LambdaQueryWrapper<Attraction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attraction::getAuditStatus, 2)
                .eq(Attraction::getStatus, 1)
                .orderByDesc(Attraction::getPopularity)
                .orderByDesc(Attraction::getBrowseCount)
                .orderByDesc(Attraction::getFavoriteCount)
                .orderByDesc(Attraction::getAverageRating);
        List<Attraction> baseCandidates = attractionMapper.selectPage(page, wrapper).getRecords();

        Integer preferTypeId = preference == null ? null : preference.getPreferAttractionTypeId();
        if (preferTypeId == null) {
            return baseCandidates;
        }

        boolean hasPreferredType = baseCandidates.stream()
                .anyMatch(a -> preferTypeId.equals(a.getTypeId()));
        if (hasPreferredType) {
            return baseCandidates;
        }

        Page<Attraction> preferredPage = new Page<>(1, PREFERRED_TYPE_APPEND_SIZE);
        LambdaQueryWrapper<Attraction> preferredWrapper = new LambdaQueryWrapper<>();
        preferredWrapper.eq(Attraction::getAuditStatus, 2)
                .eq(Attraction::getStatus, 1)
                .eq(Attraction::getTypeId, preferTypeId)
                .orderByDesc(Attraction::getPopularity)
                .orderByDesc(Attraction::getBrowseCount)
                .orderByDesc(Attraction::getFavoriteCount)
                .orderByDesc(Attraction::getAverageRating)
                .orderByDesc(Attraction::getCreateTime);
        List<Attraction> preferredCandidates = attractionMapper.selectPage(preferredPage, preferredWrapper).getRecords();
        if (preferredCandidates.isEmpty()) {
            return baseCandidates;
        }

        Map<Long, Attraction> merged = new LinkedHashMap<>();
        for (Attraction item : baseCandidates) {
            merged.put(item.getAttractionId(), item);
        }
        for (Attraction item : preferredCandidates) {
            merged.put(item.getAttractionId(), item);
        }

        List<Attraction> result = new ArrayList<>(merged.values());
        log.info("推荐候选补齐偏好类型: userPreferTypeId={}, baseSize={}, appendSize={}, mergedSize={}",
                preferTypeId, baseCandidates.size(), preferredCandidates.size(), result.size());
        return result;
    }

    private double explicitScore(Attraction item, UserPreference preference) {
        if (preference == null) {
            return 0.0;
        }
        double score = 0.0;

        Integer prefType = preference.getPreferAttractionTypeId();
        if (prefType != null && prefType.equals(item.getTypeId())) {
            score += 0.55;
        }

        String preferSeason = preference.getPreferSeason();
        String bestSeason = item.getBestSeason();
        if (StringUtils.hasText(preferSeason) && StringUtils.hasText(bestSeason)) {
            String[] seasons = preferSeason.split("[,，]");
            for (String s : seasons) {
                String season = s.trim();
                if (season.isEmpty()) {
                    continue;
                }
                if (bestSeason.contains(season)) {
                    score += 0.2;
                    break;
                }
            }
        }

        Integer budgetFloor = preference.getBudgetFloor();
        Integer budgetRange = preference.getBudgetRange();
        double ticketPrice = item.getTicketPrice() == null ? 0.0 : item.getTicketPrice().doubleValue();
        if (budgetFloor != null && budgetRange != null
                && budgetFloor <= ticketPrice && ticketPrice <= budgetRange) {
            score += 0.25;
        }

        return Math.min(score, 1.0);
    }

    private double hotScore(Attraction item, int maxBrowse, int maxFavorite, double maxRating) {
        double browse = normalize(defaultInt(item.getBrowseCount()), maxBrowse);
        double favorite = normalize(defaultInt(item.getFavoriteCount()), maxFavorite);
        double rating = normalize(item.getAverageRating() == null ? 0.0 : item.getAverageRating().doubleValue(), maxRating);
        return 0.5 * browse + 0.3 * favorite + 0.2 * rating;
    }

    private double implicitScore(Attraction item,
                                 Map<Integer, RecommendTypeBehaviorStat> behaviorByType,
                                 int maxClick,
                                 int maxStay) {
        Integer typeId = item.getTypeId();
        if (typeId == null) {
            return 0.0;
        }
        RecommendTypeBehaviorStat stat = behaviorByType.get(typeId);
        if (stat == null) {
            return 0.0;
        }
        double clickNorm = normalize(defaultInt(stat.getClickCount()), maxClick);
        double stayNorm = normalize(defaultInt(stat.getStaySeconds()), maxStay);
        return recommendProps.getBehaviorClickWeight() * clickNorm
                + recommendProps.getBehaviorStayWeight() * stayNorm;
    }

    private List<ScoredAttraction> diversify(List<ScoredAttraction> scoredItems, int maxPerType) {
        List<ScoredAttraction> result = new ArrayList<>();
        List<ScoredAttraction> overflow = new ArrayList<>();
        Map<Integer, Integer> seen = new HashMap<>();

        for (ScoredAttraction item : scoredItems) {
            Integer type = item.attraction().getTypeId();
            int count = seen.getOrDefault(type, 0);
            if (count < maxPerType) {
                result.add(item);
                seen.put(type, count + 1);
            } else {
                overflow.add(item);
            }
        }

        result.addAll(overflow);
        return result;
    }

    private double normalize(double value, double maxValue) {
        if (maxValue <= 0) {
            return 0.0;
        }
        double n = value / maxValue;
        if (n < 0) {
            return 0.0;
        }
        if (n > 1) {
            return 1.0;
        }
        return n;
    }

    private double round(double value) {
        return Math.round(value * 1_000_000d) / 1_000_000d;
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

    private record RankResult(List<Long> orderedIds, boolean behaviorEnabled) {
    }

    private record ScoredAttraction(Attraction attraction, double score, boolean typeMatch) {
    }
}
