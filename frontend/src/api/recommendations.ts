import type { RecommendationRequest, RecommendationResponse } from '../types/recommendation';

export async function requestRecommendation(payload: RecommendationRequest): Promise<RecommendationResponse> {
  const response = await fetch('/api/recommendations', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    throw new Error('추천 요청에 실패했습니다.');
  }

  return response.json();
}
