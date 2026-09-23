# MoodFit Codex 실습 작업 기록

## 1. 실습 목적

본 프로젝트는 VS Code에서 Codex를 활용하여
프로젝트 요구사항 정의부터 프로젝트 생성,
GitHub Actions 자동화 및 CI 구성까지 단계적으로 실습하기 위해 작성하였다.

이번 실습에서는 단순 자연어 요청보다
Markdown 기반 명세를 Codex에 제공하여 개발 작업의 범위와 환경을 명확하게 정의하는 방법을 학습한다.

또한 GitHub Actions를 이용하여 자동 Commit과 CI를 구성하고,
Codex와 GitHub 기반 개발 자동화 흐름을 이해하는 것을 목적으로 한다.


---

# 2. 프로젝트 개요

프로젝트명:

```text
MoodFit
```

MoodFit은 사용자가 입력한 심박수와 날씨 데이터를 기반으로
현재 기분 상태를 분석하고 음식과 음악을 추천하는 서비스이다.

주요 기술 스택은 다음과 같다.

## Frontend

```text
React
TypeScript
Vite
```

## Backend

```text
Java 21
Spring Boot
Gradle
```

## Database

```text
MySQL
```


---

# 3. 작업 단계

전체 실습은 다음 순서로 진행하였다.

```text
01. 프로젝트 요구사항 정의
        ↓
02. Codex 기반 프로젝트 생성
        ↓
03. Git Repository 생성 및 Push
        ↓
04. GitHub Actions Bot Workflow 구현
        ↓
05. GitHub Actions Bot 실행
        ↓
06. Workflow YAML 오류 수정
        ↓
07. GitHub Actions Bot Commit 확인
        ↓
08. GitHub Actions CI Workflow 구성
        ↓
09. main Branch Push 기반 CI 구성
```


---

# 4. 프로젝트 명세 작성

프로젝트 요구사항은 다음 문서에 정의하였다.

```text
01-PROJECT_SPEC.md
```

주요 정의 사항은 다음과 같다.

- 서비스 목적
- Frontend 개발 환경
- Backend 개발 환경
- Database
- 주요 기능
- API 구조
- 초기 구현 범위
- 프로젝트 디렉터리 구조
- 개발 조건

Codex에게 프로젝트 생성을 직접 요청하는 대신
`01-PROJECT_SPEC.md`의 내용을 먼저 읽도록 한 후
문서의 요구사항을 기준으로 프로젝트를 생성하였다.


---

# 5. MoodFit 프로젝트 초기 구현

Codex를 이용하여 다음 구조의 프로젝트를 생성하였다.

```text
today-v2/
│
├── backend/
├── frontend/
├── README.md
└── PROJECT_SPEC.md
```

Frontend는 React, TypeScript, Vite를 기반으로 구성하였다.

Backend는 Java 21, Spring Boot, Gradle을 기반으로 구성하였다.

주요 기능은 다음과 같다.

- 심박수 입력
- 기온 입력
- 날씨 입력
- 사용자 기분 상태 분석
- 음식 추천
- 음악 추천
- Frontend와 Backend API 연동
- MySQL 기반 추천 이력 저장 구조


---

# 6. 최초 Git Commit

프로젝트 생성 후 최초 Commit을 수행하였다.

Commit:

```text
57a4972
```

Commit Message:

```text
feat: MoodFit 프로젝트 초기 구현
```

주요 작업:

- PROJECT_SPEC.md 기반 Frontend / Backend 프로젝트 구성
- React, TypeScript, Vite 기반 화면 구현
- Java 21, Spring Boot, Gradle 기반 REST API 구성
- Controller / Service / Repository 계층 분리
- DTO / Entity 구조 분리
- MySQL 기반 추천 이력 저장 구조 구현
- 추천 API와 Frontend 연동


---

# 7. GitHub Actions Bot 구현

GitHub Actions를 이용하여
자동으로 Repository 파일을 수정하고
`github-actions[bot]` 이름으로 Commit하는 실습을 진행하였다.

작업 명세:

```text
02-GITHUB_ACTIONS_BOT.md
```

Workflow 파일:

```text
.github/workflows/bot-commit.yml
```

Workflow Trigger:

```yaml
workflow_dispatch:
```

사용자가 GitHub Actions 화면에서
`Run workflow`를 실행하면 Workflow가 동작하도록 구성하였다.


---

# 8. GitHub Actions Bot 처리 과정

Workflow의 처리 흐름은 다음과 같다.

```text
사용자
  ↓
Run workflow
  ↓
GitHub Actions
  ↓
Repository Checkout
  ↓
bot-log.txt 생성 또는 수정
  ↓
Git 사용자 설정
  ↓
git add
  ↓
git commit
  ↓
git push
  ↓
github-actions[bot] Commit 생성
```

Git Commit 작성자는 다음과 같이 설정하였다.

```text
github-actions[bot]
```

Email:

```text
41898282+github-actions[bot]@users.noreply.github.com
```


---

# 9. GitHub Actions Bot Workflow 추가

Commit:

```text
58631ca
```

Commit Message:

```text
ci: GitHub Actions Bot 워크플로우 추가
```

주요 작업:

- workflow_dispatch 기반 수동 실행 Workflow 구성
- github-actions[bot] 작성자 설정
- bot-log.txt 자동 업데이트
- 자동 Commit / Push 구성
- GITHUB_TOKEN 사용
- contents: write 권한 설정


---

# 10. Workflow YAML 오류 발생

최초 GitHub Actions 실행 과정에서
Workflow YAML 문법 오류가 발생하였다.

오류 메시지:

```text
Invalid workflow file

You have an error in your yaml syntax on line 18
```

문제가 발생한 코드는 다음과 같았다.

```yaml
run: echo "GitHub Actions Bot 실행: $(date)" >> bot-log.txt
```

YAML에서 문자열 내부의 `:`가 문법 요소로 해석되면서
Parsing Error가 발생하였다.


---

# 11. Workflow YAML 오류 수정

해당 명령을 YAML Block Scalar 방식으로 변경하였다.

수정 전:

```yaml
run: echo "GitHub Actions Bot 실행: $(date)" >> bot-log.txt
```

수정 후:

```yaml
run: |
  echo "GitHub Actions Bot 실행: $(date)" >> bot-log.txt
```

수정 Commit:

```text
539302e
```

Commit Message:

```text
fix: GitHub Actions 워크플로우 YAML 오류 수정
```


---

# 12. GitHub Actions Bot 실행 성공

Workflow 수정 후 GitHub Actions를 다시 실행하였다.

Workflow가 정상적으로 실행되면서 다음 파일이 생성되었다.

```text
bot-log.txt
```

GitHub Actions Bot이 자동으로 Commit을 생성하였다.

Commit:

```text
7acfb01
```

Commit Message:

```text
chore: GitHub Actions Bot 실행 기록 업데이트
```

Commit 작성자:

```text
github-actions[bot]
```

GitHub Repository의 Contributors에도
다음 Bot이 Contributor로 표시되는 것을 확인하였다.

```text
github-actions[bot]
```


---

# 13. GitHub Actions CI 구현

다음 단계로 main Branch에 Push가 발생하면
Frontend와 Backend의 Build를 자동으로 검증하는 CI Workflow를 구성하였다.

작업 명세:

```text
03-CI_WORKFLOW.md
```

Workflow 파일:

```text
.github/workflows/ci.yml
```

Workflow 이름:

```text
MoodFit CI
```

Trigger:

```yaml
on:
  push:
    branches:
      - main
```


---

# 14. Frontend CI

Frontend CI의 처리 순서는 다음과 같다.

```text
Repository Checkout
        ↓
Node.js 24 설정
        ↓
npm ci
        ↓
npm run build
```

Frontend는 별도의 Job으로 실행된다.

주요 검증 항목:

- Node.js 환경 구성
- package-lock.json 기반 의존성 설치
- TypeScript Compile
- Vite Build


---

# 15. Backend CI

Backend CI의 처리 순서는 다음과 같다.

```text
Repository Checkout
        ↓
Java 21 설정
        ↓
Gradle 8.9 설정
        ↓
gradle clean build
```

Backend 또한 Frontend와 별도의 Job으로 구성하였다.

주요 검증 항목:

- Java 21 환경 구성
- Gradle Build 환경 구성
- Spring Boot 프로젝트 Compile
- Test 수행
- Build 결과 확인


---

# 16. MoodFit CI Workflow Commit

Commit:

```text
3b7e159
```

Commit Message:

```text
ci: MoodFit CI 워크플로우 추가
```

주요 작업:

- main Branch Push 기반 CI Trigger 구성
- Frontend Build Job 구현
- Backend Build Job 구현
- Node.js 24 기반 Frontend 검증
- Java 21 / Gradle 8.9 기반 Backend 검증


---

# 17. Git Push 충돌 처리

MoodFit CI Commit을 Push하는 과정에서
원격 Repository에 먼저 생성된 GitHub Actions Bot Commit으로 인해
Push가 거절되었다.

원격 Repository의 변경 내용을 먼저 반영하기 위해
다음 명령을 실행하였다.

```bash
git pull --rebase --autostash origin main
```

처리 과정은 다음과 같다.

```text
Local Commit
      ↓
Remote에 새로운 Bot Commit 존재
      ↓
Push Reject
      ↓
git pull --rebase
      ↓
Remote 변경 반영
      ↓
Local Commit 재배치
      ↓
git push
      ↓
Push 성공
```

이를 통해 GitHub Actions가 생성한 자동 Commit과
개발자의 Local Commit이 함께 존재하는 상황에서
Git history를 정리하는 방법을 확인하였다.


---

# 18. 현재 Git Commit History

현재 주요 Commit은 다음과 같다.

```text
3b7e159  ci: MoodFit CI 워크플로우 추가

7acfb01  chore: GitHub Actions Bot 실행 기록 업데이트

539302e  fix: GitHub Actions 워크플로우 YAML 오류 수정

58631ca  ci: GitHub Actions Bot 워크플로우 추가

57a4972  feat: MoodFit 프로젝트 초기 구현
```


---

# 19. Markdown 문서 구조 정리

실습 과정에서 사용한 명세 문서를
작업 순서에 따라 다음과 같이 번호를 부여하였다.

```text
01-PROJECT_SPEC.md

02-GITHUB_ACTIONS_BOT.md

03-CI_WORKFLOW.md

04-WORK_LOG.md
```

각 문서의 역할은 다음과 같다.

| 파일 | 역할 |
|---|---|
| `01-PROJECT_SPEC.md` | MoodFit 프로젝트 요구사항 |
| `02-GITHUB_ACTIONS_BOT.md` | GitHub Actions Bot 구현 요구사항 |
| `03-CI_WORKFLOW.md` | GitHub Actions CI 구현 요구사항 |
| `04-WORK_LOG.md` | 전체 실습 작업 기록 |


---

# 20. 현재 프로젝트 구조

```text
today-v2/
│
├── .github/
│   └── workflows/
│       ├── bot-commit.yml
│       └── ci.yml
│
├── backend/
├── frontend/
│
├── 01-PROJECT_SPEC.md
├── 02-GITHUB_ACTIONS_BOT.md
├── 03-CI_WORKFLOW.md
├── 04-WORK_LOG.md
│
├── .gitignore
├── bot-log.txt
└── README.md
```


---

# 21. 현재까지 학습한 내용

이번 실습을 통해 다음 내용을 확인하였다.

## Codex

- 자연어 기반 개발 요청
- Markdown 명세 기반 프로젝트 생성
- Repository 구조 분석
- 오류 메시지를 이용한 수정 작업

## Git / GitHub

- Git Repository 관리
- Commit / Push
- Remote 변경 사항 반영
- Rebase
- GitHub Contributors

## GitHub Actions

- Workflow 구성
- workflow_dispatch
- push Trigger
- GitHub Runner
- GITHUB_TOKEN
- Workflow 권한
- github-actions[bot]

## CI

- Frontend Build 자동화
- Backend Build 자동화
- Push 기반 자동 검증


---

# 22. 다음 단계

다음 실습에서는 현재의 명세 기반 개발에서 한 단계 발전하여
Harness Engineering을 적용한다.

예정 구조:

```text
AGENTS.md
PROJECT.md
PLAN.md
TASKS.md
WORK_LOG.md
```

이를 통해 Codex에게 단순히 작업을 요청하는 것이 아니라

```text
Context
    ↓
Rules
    ↓
Plan
    ↓
Task
    ↓
Codex
    ↓
Build / Test
    ↓
Human Review
```

형태의 개발 환경을 구성한다.