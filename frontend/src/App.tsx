import { Activity, CloudSun, Music, Soup } from 'lucide-react';
import { FormEvent, useState } from 'react';
import type { ReactNode } from 'react';
import { requestRecommendation } from './api/recommendations';
import type { RecommendationResponse } from './types/recommendation';

function App() {
  const [heartRate, setHeartRate] = useState(76);
  const [temperature, setTemperature] = useState(22);
  const [weather, setWeather] = useState('맑음');
  const [recommendation, setRecommendation] = useState<RecommendationResponse | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setIsLoading(true);
    setErrorMessage('');

    try {
      const result = await requestRecommendation({ heartRate, temperature, weather });
      setRecommendation(result);
    } catch (error) {
      setErrorMessage(error instanceof Error ? error.message : '알 수 없는 오류가 발생했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <main className="app-shell">
      <section className="intro">
        <div>
          <p className="eyebrow">MoodFit</p>
          <h1>심박수와 날씨로 지금의 기분을 읽고 추천을 받아보세요.</h1>
        </div>
      </section>

      <section className="workspace" aria-label="추천 입력 및 결과">
        <form className="panel input-panel" onSubmit={handleSubmit}>
          <label>
            <span>
              <Activity size={18} />
              심박수
            </span>
            <input
              min="40"
              max="180"
              type="number"
              value={heartRate}
              onChange={(event) => setHeartRate(Number(event.target.value))}
            />
          </label>

          <label>
            <span>
              <CloudSun size={18} />
              기온
            </span>
            <input
              min="-30"
              max="45"
              type="number"
              value={temperature}
              onChange={(event) => setTemperature(Number(event.target.value))}
            />
          </label>

          <label>
            <span>
              <CloudSun size={18} />
              날씨
            </span>
            <select value={weather} onChange={(event) => setWeather(event.target.value)}>
              <option value="맑음">맑음</option>
              <option value="흐림">흐림</option>
              <option value="비">비</option>
              <option value="눈">눈</option>
            </select>
          </label>

          <button type="submit" disabled={isLoading}>
            {isLoading ? '분석 중' : '추천 받기'}
          </button>

          {errorMessage && <p className="error">{errorMessage}</p>}
        </form>

        <div className="panel result-panel">
          {recommendation ? (
            <>
              <div className="mood">
                <span>현재 기분</span>
                <strong>{recommendation.mood}</strong>
              </div>

              <RecommendationList icon={<Soup size={20} />} title="음식 추천" items={recommendation.foods} />
              <RecommendationList icon={<Music size={20} />} title="음악 추천" items={recommendation.music} />
            </>
          ) : (
            <div className="empty-state">
              <Activity size={36} />
              <p>입력값을 보내면 추천 결과가 여기에 표시됩니다.</p>
            </div>
          )}
        </div>
      </section>
    </main>
  );
}

interface RecommendationListProps {
  icon: ReactNode;
  title: string;
  items: string[];
}

function RecommendationList({ icon, title, items }: RecommendationListProps) {
  return (
    <section className="recommendation-list">
      <h2>
        {icon}
        {title}
      </h2>
      <ul>
        {items.map((item) => (
          <li key={item}>{item}</li>
        ))}
      </ul>
    </section>
  );
}

export default App;
