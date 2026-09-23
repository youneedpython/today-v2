# GitHub Actions CI Workflow 구현 요구사항

## 1. 목적

현재 MoodFit 프로젝트에 GitHub Actions 기반 CI(Continuous Integration)를 구성한다.

개발자가 `main` 브랜치에 코드를 Push하면 GitHub Actions가 자동으로 실행되어
Frontend와 Backend 프로젝트가 정상적으로 Build되는지 검증한다.

이번 작업에서는 배포(Deployment)는 수행하지 않는다.

이번 단계의 목적은 다음 내용을 학습하는 것이다.

- GitHub Actions의 `push` Trigger 이해
- CI(Continuous Integration)의 기본 개념 이해
- GitHub Actions Runner에서 Frontend Build 수행
- GitHub Actions Runner에서 Backend Build 및 Test 수행
- Build/Test 실패 시 GitHub Actions에서 오류를 확인하는 방법 이해
- 개발자의 Push와 자동 검증 과정 이해


---

## 2. 현재 프로젝트 구조

현재 Repository는 다음과 같은 구조를 사용한다.

```text
project-root/
│
├── .github/
│   └── workflows/
│       └── bot-commit.yml
│
├── backend/
│   ├── build.gradle
│   ├── settings.gradle
│   └── src/
│
├── frontend/
│   ├── package.json
│   ├── package-lock.json
│   └── src/
│
├── PROJECT_SPEC.md
├── README.md
└── bot-log.txt
```

기존의 `bot-commit.yml` Workflow는 수정하지 않는다.

이번 작업에서는 새로운 CI Workflow를 추가한다.


---

## 3. 생성할 Workflow 파일

다음 파일을 생성한다.

```text
.github/workflows/ci.yml
```

작업 완료 후 Workflow 관련 구조는 다음과 같아야 한다.

```text
.github/
└── workflows/
    ├── bot-commit.yml
    └── ci.yml
```


---

## 4. Workflow 이름

GitHub Actions 화면에서 쉽게 구분할 수 있도록 Workflow 이름은 다음으로 설정한다.

```yaml
name: MoodFit CI
```


---

## 5. 실행 조건

이번 CI Workflow는 `main` 브랜치에 Push가 발생하면 자동으로 실행한다.

다음 Trigger를 사용한다.

```yaml
on:
  push:
    branches:
      - main
```

이번 단계에서는 다음 Trigger는 사용하지 않는다.

```text
workflow_dispatch
pull_request
schedule
```

즉, 사용자가 GitHub Actions 화면에서 직접 실행하지 않아도
`main` 브랜치에 Push가 발생하면 자동으로 실행되어야 한다.


---

## 6. Workflow 권한

이번 Workflow는 Repository 내용을 수정하지 않는다.

Build와 Test만 수행한다.

따라서 Repository 쓰기 권한은 필요하지 않다.

다음과 같이 최소 권한을 사용한다.

```yaml
permissions:
  contents: read
```

이번 Workflow에서는 다음 권한을 사용하지 않는다.

```yaml
contents: write
```

또한 Personal Access Token(PAT)은 사용하지 않는다.


---

## 7. Runner

GitHub에서 제공하는 Ubuntu Runner를 사용한다.

```yaml
runs-on: ubuntu-latest
```


---

# 8. 전체 CI 구조

이번 CI Workflow는 Frontend와 Backend를 각각 별도의 Job으로 실행한다.

전체 구조는 다음과 같다.

```text
Developer
   │
   │ git push origin main
   ↓
GitHub Repository
   │
   ↓
GitHub Actions
   │
   ├─────────────────────────┐
   │                         │
   ↓                         ↓
Frontend CI               Backend CI
   │                         │
Checkout                  Checkout
   │                         │
Node.js 설정              Java 21 설정
   │                         │
npm ci                    Gradle 설정
   │                         │
npm run build             gradle clean build
   │                         │
   ↓                         ↓
Success / Failure        Success / Failure
```

Frontend와 Backend Job은 서로 독립적으로 실행되도록 구성한다.


---

# 9. Frontend Job

Frontend Job의 이름은 다음과 같이 구성한다.

```yaml
frontend:
```

또는 사람이 읽기 쉬운 Job 이름을 추가할 수 있다.

```yaml
name: Frontend Build
```

Frontend Job은 다음 순서로 실행한다.

```text
1. Repository Checkout
2. Node.js 설정
3. npm 의존성 설치
4. Frontend Build
```


---

## 9.1 Repository Checkout

현재 Repository의 파일을 Runner로 가져오기 위해 GitHub 공식 Checkout Action을 사용한다.

```yaml
uses: actions/checkout@v7
```


---

## 9.2 Node.js 설정

GitHub 공식 Node Setup Action을 사용한다.

```yaml
uses: actions/setup-node@v7
```

Node.js 버전은 다음을 사용한다.

```text
24
```

예상 설정:

```yaml
with:
  node-version: 24
  cache: npm
  cache-dependency-path: frontend/package-lock.json
```

`frontend/package-lock.json`이 존재하므로 npm Cache 설정에 이를 사용한다.


---

## 9.3 Frontend 의존성 설치

Frontend 디렉터리에서 다음 명령을 실행한다.

```bash
npm ci
```

`npm install` 대신 `npm ci`를 사용한다.

이유:

- `package-lock.json`을 기준으로 정확한 의존성을 설치한다.
- CI 환경에서 재현성이 높다.
- 기존 lock 파일의 의존성 버전을 변경하지 않는다.

명령 실행 위치는 다음이다.

```text
frontend/
```


---

## 9.4 Frontend Build

Frontend 디렉터리에서 다음 명령을 실행한다.

```bash
npm run build
```

현재 Frontend 프로젝트의 `package.json`에는 다음 Build Script가 존재한다.

```text
tsc --noEmit && vite build
```

따라서 이 단계에서는 다음 항목을 함께 검증할 수 있다.

```text
TypeScript Compile Check
        ↓
Vite Build
```

Build 실패 시 해당 Job은 실패 처리되어야 한다.


---

# 10. Backend Job

Backend Job은 다음 순서로 실행한다.

```text
1. Repository Checkout
2. Java 21 설정
3. Gradle 설정
4. Backend Build 및 Test
```


---

## 10.1 Repository Checkout

다음 Action을 사용한다.

```yaml
uses: actions/checkout@v7
```


---

## 10.2 Java 설정

현재 Backend 프로젝트는 Java 21을 사용한다.

GitHub 공식 Java Setup Action을 사용한다.

```yaml
uses: actions/setup-java@v6
```

Java 배포판은 Eclipse Temurin을 사용한다.

```yaml
with:
  distribution: temurin
  java-version: '21'
```


---

## 10.3 Gradle 설정

현재 Backend 프로젝트에는 Gradle Wrapper(`gradlew`)가 존재하지 않는다.

따라서 이번 CI에서는 Gradle Wrapper를 새로 생성하지 않고,
GitHub Actions에서 Gradle을 직접 설치하여 사용한다.

Gradle 공식 Action을 사용한다.

```yaml
uses: gradle/actions/setup-gradle@v6
```

Gradle 버전은 다음으로 지정한다.

```text
8.9
```

예상 설정:

```yaml
with:
  gradle-version: '8.9'
```

Gradle 버전 값은 YAML 숫자 변환 문제를 방지하기 위해 문자열로 작성한다.


---

## 10.4 Backend Build 및 Test

Backend 디렉터리에서 다음 명령을 실행한다.

```bash
gradle clean build
```

작업 위치는 다음이다.

```text
backend/
```

Gradle `build` Task는 프로젝트 Compile과 Test를 포함하여 실행한다.

현재 테스트 코드가 없더라도 Build가 정상적으로 수행되어야 한다.

향후 테스트 코드가 추가되면 동일한 CI 과정에서 자동으로 테스트가 실행되어야 한다.


---

# 11. Working Directory 사용

Frontend와 Backend 명령 실행 시 Repository 루트에서
다음과 같이 긴 명령을 작성하는 것보다:

```bash
cd frontend
npm ci
npm run build
```

GitHub Actions의 `working-directory`를 사용하는 것을 권장한다.

예:

```yaml
working-directory: frontend
```

Backend도 동일하게 적용한다.

```yaml
working-directory: backend
```

각 Step이 어느 프로젝트에서 실행되는지 명확하게 보이도록 작성한다.


---

# 12. 구현 조건

다음 조건을 반드시 지킨다.

1. 다음 Workflow 파일을 새로 생성한다.

```text
.github/workflows/ci.yml
```

2. 기존 파일은 수정하지 않는다.

```text
.github/workflows/bot-commit.yml
```

3. Workflow 이름은 다음으로 한다.

```text
MoodFit CI
```

4. `main` 브랜치 Push 시 자동 실행한다.

```yaml
on:
  push:
    branches:
      - main
```

5. Repository 쓰기 작업은 수행하지 않는다.

6. 권한은 최소 권한으로 설정한다.

```yaml
permissions:
  contents: read
```

7. 자동 Commit을 생성하지 않는다.

8. 자동 Push를 수행하지 않는다.

9. `github-actions[bot]` Commit을 생성하지 않는다.

10. Frontend와 Backend는 별도의 Job으로 구성한다.

11. Frontend는 다음 순서로 검증한다.

```text
Checkout
→ Node.js 24
→ npm ci
→ npm run build
```

12. Backend는 다음 순서로 검증한다.

```text
Checkout
→ Java 21
→ Gradle 8.9
→ gradle clean build
```

13. Frontend Build에는 `frontend/package-lock.json`을 이용한 npm Cache를 적용한다.

14. Backend 프로젝트 파일을 CI 구현을 위해 임의로 수정하지 않는다.

15. Gradle Wrapper를 이번 단계에서 새로 생성하지 않는다.

16. Docker를 사용하지 않는다.

17. AWS 배포를 수행하지 않는다.

18. 외부 서버에 배포하지 않는다.

19. 테스트 실패 또는 Build 실패를 무시하지 않는다.

20. 실패한 경우 GitHub Actions Job이 Failure 상태가 되어야 한다.


---

# 13. 이번 단계에서 생성하지 않을 기능

이번 실습에서는 다음 기능을 구현하지 않는다.

```text
자동 Commit

자동 Push

Pull Request 자동 생성

Pull Request 자동 Merge

Docker Build

Docker Image Push

ECR Push

AWS 배포

ECS 배포

CloudFormation

배포 환경 구성

Slack 알림

Email 알림

Release 생성
```

이번 단계의 목적은 오직 다음이다.

```text
Push
→ Build
→ Test
→ 결과 확인
```


---

# 14. 예상 프로젝트 구조

작업 완료 후 다음과 같은 구조가 되어야 한다.

```text
project-root/
│
├── .github/
│   └── workflows/
│       ├── bot-commit.yml
│       └── ci.yml
│
├── backend/
│
├── frontend/
│
├── PROJECT_SPEC.md
├── README.md
└── bot-log.txt
```


---

# 15. 예상 CI 실행 흐름

개발자가 코드를 수정한 후 다음 명령을 실행한다.

```bash
git add .
git commit -m "작업 내용"
git push origin main
```

Push가 완료되면 다음 과정이 자동으로 실행된다.

```text
git push
   ↓
GitHub Repository
   ↓
main Branch 변경 감지
   ↓
MoodFit CI 실행
   │
   ├──────────────────────────────┐
   │                              │
   ↓                              ↓
Frontend Build                 Backend Build
   │                              │
Node.js 24                     Java 21
   │                              │
npm ci                         Gradle 8.9
   │                              │
npm run build                  gradle clean build
   │                              │
   ↓                              ↓
Success / Failure             Success / Failure
```


---

# 16. GitHub에서 확인 방법

Push 후 GitHub Repository에서 다음 메뉴로 이동한다.

```text
Repository
→ Actions
```

다음 Workflow가 나타나야 한다.

```text
MoodFit CI
```

Workflow를 선택하면 다음 Job을 확인할 수 있어야 한다.

```text
Frontend Build

Backend Build
```

각 Job에서 수행된 Step과 로그를 확인한다.


---

# 17. 성공 조건

다음 조건을 모두 만족하면 CI 구축이 성공한 것으로 판단한다.

## Frontend

```text
Repository Checkout 성공

Node.js 설정 성공

npm ci 성공

npm run build 성공
```

## Backend

```text
Repository Checkout 성공

Java 21 설정 성공

Gradle 8.9 설정 성공

gradle clean build 성공
```

최종 Workflow 상태가 다음이어야 한다.

```text
Success
```


---

# 18. 실패 실습

CI Workflow가 정상 동작한 후,
CI가 왜 필요한지 이해하기 위해 의도적으로 간단한 오류를 만들어 본다.

단, 처음부터 오류를 만들지 않는다.

먼저 정상 CI 성공을 확인한 후 진행한다.

예를 들어 Frontend TypeScript 코드에 Compile Error를 발생시킨 후 Push한다.

그러면 다음 흐름을 확인한다.

```text
잘못된 코드 Push
        ↓
GitHub Actions 실행
        ↓
npm run build
        ↓
TypeScript Compile Error
        ↓
Frontend Build 실패
        ↓
MoodFit CI Failure
```

이후 코드를 수정하여 다시 Push한다.

```text
오류 수정
   ↓
Commit
   ↓
Push
   ↓
CI 재실행
   ↓
Success
```

이 실습을 통해 CI가 단순 자동 실행 도구가 아니라
잘못된 코드가 통합되는 것을 조기에 발견하는 검증 장치임을 확인한다.


---

# 19. Codex 작업 규칙

이 문서를 읽은 Codex는 다음 규칙을 따른다.

1. 먼저 현재 Repository 구조를 확인한다.

2. `frontend/package.json`을 확인하여 실제 Build Script를 확인한다.

3. `frontend/package-lock.json` 존재 여부를 확인한다.

4. `backend/build.gradle`을 확인하여 Java 및 Gradle 프로젝트 구조를 확인한다.

5. Gradle Wrapper 존재 여부를 확인한다.

6. 요구사항과 실제 Repository 구조가 충돌하면 임의로 수정하지 말고 사용자에게 설명한다.

7. 기존 프로젝트 소스 코드는 수정하지 않는다.

8. 기존 `bot-commit.yml`은 수정하지 않는다.

9. 새로운 Workflow 파일인 `ci.yml`만 생성한다.

10. 필요하지 않은 Action을 추가하지 않는다.

11. CI에서 Repository Commit 또는 Push를 수행하지 않는다.

12. Build 실패를 강제로 성공 처리하지 않는다.

13. 요구사항 이상의 CD 기능을 구현하지 않는다.

14. 구현 완료 후 Workflow 전체 구조를 다시 검토한다.


---

# 20. 작업 완료 후 보고

Codex는 구현이 끝난 후 다음 내용을 설명한다.

## 1. 생성한 파일

어떤 파일을 생성했는지 설명한다.

## 2. Trigger

왜 `push` Trigger를 사용했는지 설명한다.

## 3. Frontend CI

다음 과정이 어떻게 수행되는지 설명한다.

```text
Node.js
→ npm ci
→ npm run build
```

## 4. Backend CI

다음 과정이 어떻게 수행되는지 설명한다.

```text
Java 21
→ Gradle 8.9
→ gradle clean build
```

## 5. Job 분리

Frontend와 Backend를 별도 Job으로 구성한 이유를 설명한다.

## 6. CI 결과 확인 방법

GitHub Actions에서 Success와 Failure를 확인하는 방법을 설명한다.

## 7. 실행 방법

어떤 행동을 하면 Workflow가 자동 실행되는지 설명한다.


---

# 21. 최종 목표

이번 작업의 최종 목표는 다음 흐름이 자동으로 동작하는 것이다.

```text
Developer
   ↓
Code 수정
   ↓
Git Commit
   ↓
Git Push
   ↓
GitHub Actions
   ↓
┌───────────────────┐
│ Frontend Build    │
│ Backend Build     │
└───────────────────┘
   ↓
Success / Failure
```

이번 단계에서는 CI까지만 구현한다.

배포(CD)는 구현하지 않는다.