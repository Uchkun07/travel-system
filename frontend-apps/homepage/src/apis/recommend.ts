import { post } from "./request";
import type { ApiResponse, PageResponse } from "./common";
import type { AttractionCard } from "./attraction";

export interface RecommendHomeRequest {
  pageNum: number;
  pageSize: number;
}

export interface RecommendHomeResponse {
  requestId: string;
  recVersion: string;
  behaviorEnabled: boolean;
  page: PageResponse<AttractionCard>;
}

export interface RecommendTrackRequest {
  attractionId: number;
  eventType: "impression" | "click" | "stay";
  requestId?: string;
  position?: number;
  staySeconds?: number;
  sourcePage?: string;
  recVersion?: string;
}

export function getHomeRecommendations(params: RecommendHomeRequest) {
  return post<ApiResponse<RecommendHomeResponse>>(
    "/api/recommend/home",
    params,
  );
}

export function trackRecommendEvent(data: RecommendTrackRequest) {
  return post<ApiResponse<null>>("/api/recommend/track", data);
}
