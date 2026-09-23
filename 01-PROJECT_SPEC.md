# 프로젝트 개요

## 1. 서비스명
MoodFit

## 2. 서비스 목적
사용자의 심박수와 날씨 데이터를 이용하여
현재 기분 상태를 추정하고 음식과 음악을 추천한다.

## 3. 개발 환경

### Frontend
- React
- TypeScript
- Vite

### Backend
- Java 21
- Spring Boot
- Gradle

### Database
- MySQL

## 4. 주요 기능

1. 사용자 정보 입력
2. 심박수 입력
3. 날씨 정보 조회
4. 사용자 상태 분석
5. 음식 추천
6. 음악 추천

## 5. 초기 구현 범위

실제 스마트워치 연동은 하지 않는다.

심박수는 사용자가 직접 입력한다.

날씨 API는 초기에는 Mock 데이터로 구현한다.

## 6. API

POST /api/recommendations

Request:
- heartRate
- temperature
- weather

Response:
- mood
- foods
- music

## 7. 개발 조건

- Controller / Service / Repository 계층을 구분한다.
- DTO와 Entity를 분리한다.
- 프론트엔드와 백엔드를 별도 디렉터리로 구성한다.
- README.md에 실행 방법을 작성한다.
