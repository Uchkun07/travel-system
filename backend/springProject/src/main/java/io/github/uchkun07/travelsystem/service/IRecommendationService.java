package io.github.uchkun07.travelsystem.service;

import io.github.uchkun07.travelsystem.dto.RecommendHomeRequest;
import io.github.uchkun07.travelsystem.dto.RecommendHomeResponse;
import io.github.uchkun07.travelsystem.dto.RecommendTrackRequest;

public interface IRecommendationService {

    RecommendHomeResponse getHomeRecommendations(Long userId, RecommendHomeRequest request);

    void track(Long userId, RecommendTrackRequest request, String userAgent);
}
