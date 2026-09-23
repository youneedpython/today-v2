# MoodFit

MoodFit은 사용자가 입력한 심박수와 날씨 데이터를 바탕으로 현재 기분 상태를 추정하고 음식과 음악을 추천하는 서비스입니다.

## 기술 스택

- Frontend: React, TypeScript, Vite
- Backend: Java 21, Spring Boot, Gradle
- Database: MySQL

## 프로젝트 구조

```text
today-v2/
  frontend/                 # React + TypeScript + Vite
  backend/                  # Java 21 + Spring Boot + Gradle
  PROJECT_SPEC.md
  README.md
```

## 주요 기능

- 사용자 심박수 입력
- 날씨 정보 입력
- 사용자 상태 분석
- 음식 추천
- 음악 추천

현재 초기 구현에서는 실제 스마트워치와 외부 날씨 API를 연동하지 않습니다. 심박수와 날씨는 사용자가 직접 입력합니다.

## API

### POST `/api/recommendations`

Request:

```json
{
  "heartRate": 76,
  "temperature": 22,
  "weather": "맑음"
}
```

Response:

```json
{
  "mood": "상쾌함",
  "foods": ["샌드위치", "요거트 볼", "아이스 아메리카노"],
  "music": ["Morning Indie", "Fresh Pop", "City Walk"]
}
```

## 실행 방법

### 필요 환경

- Java 21
- Gradle
- Node.js
- npm
- MySQL

### 1. MySQL 준비

```sql
CREATE DATABASE moodfit CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'moodfit'@'localhost' IDENTIFIED BY 'moodfit';
GRANT ALL PRIVILEGES ON moodfit.* TO 'moodfit'@'localhost';
FLUSH PRIVILEGES;
```

DB 접속 정보는 `backend/src/main/resources/application.yml`에서 변경할 수 있습니다.

### 2. 백엔드 실행

```bash
cd backend
gradle bootRun
```

백엔드는 `http://localhost:8080`에서 실행됩니다.

백엔드는 API 서버이므로 브라우저에서 `http://localhost:8080` 루트 주소를 열면 화면이 나오지 않을 수 있습니다. 화면은 프론트엔드 실행 후 `http://127.0.0.1:5173`에서 확인합니다.

### 3. 프론트엔드 실행

```bash
cd frontend
npm install
npm run dev
```

프론트엔드는 `http://127.0.0.1:5173`에서 실행됩니다.

Vite 개발 서버는 `/api` 요청을 `http://localhost:8080`으로 프록시합니다.

## 동작 확인

백엔드가 실행 중일 때 아래 요청으로 추천 API를 확인할 수 있습니다.

```bash
curl -X POST http://localhost:8080/api/recommendations \
  -H "Content-Type: application/json" \
  -d "{\"heartRate\":76,\"temperature\":22,\"weather\":\"맑음\"}"
```

프론트엔드는 화면에서 심박수, 기온, 날씨를 입력한 뒤 `추천 받기` 버튼을 누르면 추천 결과를 표시합니다.
