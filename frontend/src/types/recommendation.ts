export interface RecommendationRequest {
  heartRate: number;
  temperature: number;
  weather: string;
}

export interface RecommendationResponse {
  mood: string;
  foods: string[];
  music: string[];
}
