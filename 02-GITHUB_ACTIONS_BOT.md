# GitHub Actions Bot 구현 요구사항

## 1. 목적

GitHub Actions를 이용하여 Repository의 파일을 자동으로 수정하고,
`github-actions[bot]` 이름으로 변경 사항을 Commit 및 Push하는
간단한 자동화 Workflow를 구현한다.

이번 작업에서는 다음 내용을 학습한다.

- GitHub Actions Workflow의 기본 구조
- `workflow_dispatch`를 이용한 수동 실행
- GitHub Actions Runner에서 Repository 파일 수정
- `GITHUB_TOKEN`을 이용한 Repository 쓰기
- `github-actions[bot]` 이름으로 Commit 생성
- GitHub Actions의 자동 Commit 및 Push 과정
- Workflow 권한 설정 방법


---

## 2. 작업 범위

현재 Repository에 GitHub Actions Workflow를 추가한다.

다음 Workflow 파일을 생성한다.

```text
.github/
└── workflows/
    └── bot-commit.yml
```

Workflow가 실행되면 Repository 루트의 다음 파일을 생성하거나 수정한다.

```text
bot-log.txt
```

기존 프로젝트의 Frontend, Backend 및 기타 소스 코드는 수정하지 않는다.


---

## 3. Workflow 실행 방식

첫 번째 실습에서는 자동 Trigger를 사용하지 않는다.

GitHub Actions 화면에서 사용자가 직접 실행할 수 있도록
다음 Trigger를 사용한다.

```yaml
on:
  workflow_dispatch:
```

이번 단계에서는 다음 Trigger를 사용하지 않는다.

```text
push
pull_request
schedule
```

사용자가 GitHub의 Actions 화면에서 직접 `Run workflow` 버튼을 눌렀을 때만 실행되도록 한다.


---

## 4. Workflow 이름

GitHub Actions 화면에서 쉽게 확인할 수 있도록
Workflow 이름은 다음과 같이 설정한다.

```yaml
name: GitHub Actions Bot Commit
```


---

## 5. GitHub Actions 권한

Workflow가 Repository의 파일을 수정하고 Push할 수 있도록
다음 권한을 설정한다.

```yaml
permissions:
  contents: write
```

별도의 Personal Access Token(PAT)은 사용하지 않는다.

GitHub Actions에서 기본적으로 제공되는
`GITHUB_TOKEN`을 이용하여 현재 Repository에 접근한다.


---

## 6. Runner

GitHub에서 제공하는 Ubuntu Runner를 사용한다.

```yaml
runs-on: ubuntu-latest
```


---

## 7. Repository Checkout

Workflow에서 현재 Repository의 파일을 사용할 수 있도록
GitHub 공식 Checkout Action을 사용한다.

```yaml
- name: 저장소 Checkout
  uses: actions/checkout@v4
```

불필요한 외부 Action은 추가하지 않는다.


---

## 8. 자동 수행 작업

Workflow가 실행되면 다음 순서대로 작업한다.

```text
1. Repository Checkout
        ↓
2. bot-log.txt 수정
        ↓
3. Git 사용자 설정
        ↓
4. 변경 사항 확인
        ↓
5. git add
        ↓
6. git commit
        ↓
7. git push
```


---

# 9. Step 1 - Repository Checkout

현재 Repository를 GitHub Actions Runner에 Checkout한다.

다음 Action을 사용한다.

```yaml
uses: actions/checkout@v4
```

Checkout 이후 Workflow에서 Repository의 파일을 읽고 수정할 수 있어야 한다.


---

# 10. Step 2 - bot-log.txt 수정

Repository 루트의 다음 파일을 사용한다.

```text
bot-log.txt
```

파일이 없으면 새로 생성한다.

파일이 이미 존재하면 기존 내용을 삭제하지 않고,
새로운 실행 기록을 마지막 줄에 추가한다.

실행할 때마다 다음 형식으로 기록한다.

```text
GitHub Actions Bot 실행: 실행시간
```

예:

```text
GitHub Actions Bot 실행: Wed Sep 23 07:10:20 UTC 2026
```

Linux의 `date` 명령을 사용할 수 있다.

예:

```bash
echo "GitHub Actions Bot 실행: $(date)" >> bot-log.txt
```


---

# 11. Step 3 - Git 사용자 설정

GitHub Actions에서 생성되는 Commit의 작성자를
다음 Bot 계정으로 설정한다.

## user.name

```text
github-actions[bot]
```

## user.email

```text
41898282+github-actions[bot]@users.noreply.github.com
```

Git 설정은 다음 명령을 사용한다.

```bash
git config user.name "github-actions[bot]"
git config user.email "41898282+github-actions[bot]@users.noreply.github.com"
```


---

# 12. Step 4 - 변경 사항 확인

Commit을 생성하기 전에 실제 변경 사항이 존재하는지 확인한다.

변경 사항이 없는 경우에는 불필요한 Commit을 생성하지 않는다.

예를 들어 다음 명령을 활용할 수 있다.

```bash
git status --short
```

또는 다음과 같은 조건 처리를 사용할 수 있다.

```bash
if git diff --quiet; then
    echo "변경 사항이 없습니다."
else
    echo "변경 사항이 있습니다."
fi
```

단, `bot-log.txt`를 새로 생성한 경우에는
Untracked File도 변경 사항으로 인식할 수 있도록 구현한다.


---

# 13. Step 5 - Staging

변경된 `bot-log.txt` 파일만 Staging Area에 추가한다.

```bash
git add bot-log.txt
```

이번 실습에서는 다음과 같이 모든 파일을 추가하지 않는다.

```bash
git add .
```

기존 프로젝트 파일이 실수로 Commit되는 것을 방지하기 위해
변경 대상 파일을 명시적으로 지정한다.


---

# 14. Step 6 - Commit

Commit Message는 다음과 같이 작성한다.

```text
chore: GitHub Actions Bot 실행 기록 업데이트
```

예:

```bash
git commit -m "chore: GitHub Actions Bot 실행 기록 업데이트"
```

Commit 작성자는 앞에서 설정한 다음 계정으로 기록되어야 한다.

```text
github-actions[bot]
```


---

# 15. Step 7 - Push

생성된 Commit을 현재 Repository의 동일한 브랜치로 Push한다.

기본적으로 다음 명령을 사용할 수 있다.

```bash
git push
```

별도의 Personal Access Token을 직접 생성하지 않는다.

GitHub Actions에서 제공되는 `GITHUB_TOKEN`을 사용하여 Push한다.


---

# 16. 구현 조건

다음 조건을 반드시 지킨다.

1. Workflow 파일은 다음 위치에 생성한다.

```text
.github/workflows/bot-commit.yml
```

2. Workflow 이름은 다음으로 설정한다.

```text
GitHub Actions Bot Commit
```

3. Trigger는 `workflow_dispatch`만 사용한다.

4. 이번 단계에서는 `push` Trigger를 사용하지 않는다.

5. 다음 권한을 설정한다.

```yaml
permissions:
  contents: write
```

6. Runner는 다음을 사용한다.

```yaml
ubuntu-latest
```

7. Repository Checkout에는 다음 Action을 사용한다.

```yaml
actions/checkout@v4
```

8. 별도의 Personal Access Token은 생성하거나 사용하지 않는다.

9. GitHub Actions에서 제공되는 기본 `GITHUB_TOKEN`을 사용한다.

10. Commit 작성자는 반드시 다음과 같이 설정한다.

```text
github-actions[bot]
```

11. Commit 이메일은 다음을 사용한다.

```text
41898282+github-actions[bot]@users.noreply.github.com
```

12. Commit Message는 다음으로 한다.

```text
chore: GitHub Actions Bot 실행 기록 업데이트
```

13. Repository의 기존 프로젝트 코드는 수정하지 않는다.

14. 불필요한 라이브러리를 설치하지 않는다.

15. 불필요한 파일을 생성하지 않는다.

16. Workflow 파일과 `bot-log.txt` 이외의 파일은 수정하지 않는다.

17. 변경 사항이 없는 경우 불필요한 Commit을 생성하지 않는다.


---

# 17. 예상 프로젝트 구조

작업 완료 후 Repository의 관련 구조는 다음과 같아야 한다.

```text
project-root/
│
├── .github/
│   └── workflows/
│       └── bot-commit.yml
│
├── bot-log.txt
│
├── 기존 프로젝트 파일...
│
└── README.md
```

`bot-log.txt`는 최초 Workflow 실행 시 생성되어도 된다.


---

# 18. 예상 Workflow 구조

구현되는 GitHub Actions Workflow의 전체 흐름은 다음과 같다.

```text
사용자
  │
  │ Run workflow
  ↓
GitHub Actions
  │
  ↓
Ubuntu Runner 생성
  │
  ↓
Repository Checkout
  │
  ↓
bot-log.txt 생성 또는 수정
  │
  ↓
Git 사용자 설정
  │
  ├─ user.name
  │    github-actions[bot]
  │
  └─ user.email
       41898282+github-actions[bot]@users.noreply.github.com
  │
  ↓
변경 사항 확인
  │
  ↓
git add bot-log.txt
  │
  ↓
git commit
  │
  ↓
git push
  │
  ↓
GitHub Repository
  │
  ↓
github-actions[bot] Commit 생성
```


---

# 19. 예상 실행 결과

Workflow를 한 번 실행하면 Repository에 다음과 같은 파일이 생성된다.

```text
bot-log.txt
```

파일 내용 예:

```text
GitHub Actions Bot 실행: Wed Sep 23 07:10:20 UTC 2026
```

Workflow를 다시 실행하면 기존 내용 아래에 새로운 기록이 추가된다.

예:

```text
GitHub Actions Bot 실행: Wed Sep 23 07:10:20 UTC 2026
GitHub Actions Bot 실행: Wed Sep 23 07:15:42 UTC 2026
GitHub Actions Bot 실행: Wed Sep 23 07:20:11 UTC 2026
```


---

# 20. Git Commit 예상 결과

GitHub Repository의 Commit History에는 다음과 같은 Commit이 추가되어야 한다.

```text
chore: GitHub Actions Bot 실행 기록 업데이트
```

Commit 작성자는 다음으로 표시되어야 한다.

```text
github-actions[bot]
```

예상 Commit History:

```text
사용자
feat: 프로젝트 초기 구현

github-actions[bot]
chore: GitHub Actions Bot 실행 기록 업데이트
```


---

# 21. GitHub에서 Workflow 실행 방법

Workflow 구현 후 다음 순서로 테스트한다.

```text
GitHub Repository
        ↓
Actions
        ↓
GitHub Actions Bot Commit
        ↓
Run workflow
        ↓
Run workflow 버튼 클릭
```


---

# 22. 검증 항목

Workflow 실행 후 다음 사항을 확인한다.

## 22.1 Workflow 표시 확인

GitHub Repository의 다음 메뉴로 이동한다.

```text
Repository
→ Actions
```

다음 Workflow가 표시되어야 한다.

```text
GitHub Actions Bot Commit
```


## 22.2 Workflow 실행 확인

다음을 선택한다.

```text
GitHub Actions Bot Commit
→ Run workflow
```

Workflow 실행 결과가 성공 상태인지 확인한다.


## 22.3 bot-log.txt 확인

Repository 루트에 다음 파일이 존재하는지 확인한다.

```text
bot-log.txt
```

파일 안에 Workflow 실행 시간이 기록되어 있어야 한다.


## 22.4 Commit 확인

Repository의 Commit History를 확인한다.

다음 Commit Message가 존재해야 한다.

```text
chore: GitHub Actions Bot 실행 기록 업데이트
```


## 22.5 작성자 확인

해당 Commit 작성자가 다음과 같이 표시되는지 확인한다.

```text
github-actions[bot]
```


---

# 23. 오류 발생 시 확인 사항

Workflow 실행 중 Push 권한 관련 오류가 발생하면 다음 항목을 확인한다.

```text
Repository
→ Settings
→ Actions
→ General
→ Workflow permissions
```

Repository 정책상 필요한 경우
Workflow가 Repository에 쓰기 작업을 수행할 수 있도록 설정한다.

단, 먼저 Workflow에 다음 설정이 존재하는지 확인한다.

```yaml
permissions:
  contents: write
```

불필요하게 Personal Access Token을 생성하여 해결하지 않는다.


---

# 24. 이번 단계에서 구현하지 않을 기능

이번 실습에서는 다음 기능을 구현하지 않는다.

```text
push 이벤트 자동 실행

pull_request 이벤트 자동 실행

schedule 자동 실행

Docker Build

Docker Image Push

Docker Image Tag 자동 변경

AWS 배포

CI/CD Pipeline

자동 Pull Request 생성

자동 Merge

Codex 자동 실행

외부 API 호출

Slack 알림
```

이 기능들은 후속 실습에서 구현한다.


---

# 25. 작업 완료 후 보고

Codex는 구현을 완료한 후 다음 내용을 사용자에게 설명한다.

## 1. 생성한 파일

생성하거나 수정한 파일 목록을 설명한다.


## 2. Workflow Trigger

왜 `workflow_dispatch`를 사용했는지 설명한다.


## 3. Workflow 처리 과정

다음 처리 순서를 설명한다.

```text
Checkout
→ 파일 수정
→ Git 설정
→ Commit
→ Push
```


## 4. 권한

다음 설정의 역할을 설명한다.

```yaml
permissions:
  contents: write
```


## 5. GITHUB_TOKEN

GitHub Actions에서 `GITHUB_TOKEN`이 어떤 역할을 하는지 설명한다.


## 6. github-actions[bot]

왜 Commit 작성자가 다음과 같이 나타나는지 설명한다.

```text
github-actions[bot]
```


## 7. 실행 방법

GitHub Actions 화면에서 Workflow를 직접 실행하는 방법을 설명한다.


---

# 26. Codex 작업 규칙

이 문서를 읽은 Codex는 다음 규칙을 따른다.

1. 먼저 현재 Repository 구조를 확인한다.

2. 기존 프로젝트 파일을 임의로 수정하지 않는다.

3. 요구사항에 없는 기능을 추가하지 않는다.

4. 새로운 라이브러리를 설치하지 않는다.

5. 작업에 필요한 최소한의 파일만 생성한다.

6. GitHub Actions Workflow 문법이 올바른지 확인한다.

7. Workflow 파일 생성 후 내용을 다시 검토한다.

8. 구현 범위를 임의로 확대하지 않는다.

9. 현재 작업 완료 후 다음 단계의 CI/CD 기능을 미리 구현하지 않는다.

10. 작업이 완료되면 변경 사항을 요약해서 설명한다.


---

# 27. 최종 목표

이번 작업의 최종 목표는 다음 흐름이 정상적으로 동작하는 것이다.

```text
사용자가 GitHub Actions 실행
              ↓
      Workflow 실행
              ↓
       파일 자동 수정
              ↓
          Git Commit
              ↓
       github-actions[bot]
              ↓
           Git Push
              ↓
       Repository 반영
```

이번 단계에서는 위 기능까지만 구현한다.

추가적인 CI/CD 자동화 기능은 구현하지 않는다.