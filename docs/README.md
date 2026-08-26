# Haru - 하루의 일기를 사진으로 기록하는 서비스
<img width="1024" height="559" alt="image" src="https://github.com/user-attachments/assets/89c47885-8e97-4719-973a-63ca1bbf21c3" />

<br>

## 📖 Table of Contents
- [[1] About the Project](#1-about-the-project)
- [[2] Technologies](#2-technologies)
- [[3] Architecture & ERD](#3-architecture--erd)
- [[4] Folder Structure](#4-folder-structure)
- [[5] Our Team](#5-our-team)


<br>

## [1] About the Project

`🌟 당신의 평범한 하루가 사진 한 장으로 기억될 수 있도록`

- **사진으로 쓰는 하루 일기**: 사진과 함께 하루의 감정을 기록하고 안전하게 저장합니다.
- **커뮤니티 공간**: 사용자 간 하루의 일상을 공유할 수 있도록 소통의 장 제공
- **공감과 교류**: 다른 사람들의 하루를 구경하고 댓글을 통해 서로의 일상에 공감과 응원을 나눕니다.

<br>

## [2] Technologies

### Language
<img src="https://img.shields.io/badge/java 21-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white">

### Framework & Library
<img src="https://img.shields.io/badge/springboot-%236DB33F.svg?style=for-the-badge&logo=springboot&logoColor=white"> <img src="https://img.shields.io/badge/springsecurity-%236DB33F.svg?style=for-the-badge&logo=springsecurity&logoColor=white"> <img src="https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white"> <img src="https://img.shields.io/badge/json-000000?style=for-the-badge&logo=json&logoColor=white">

### DataBase
<img src="https://img.shields.io/badge/mysql-%234479A1.svg?style=for-the-badge&logo=mysql&logoColor=white">

### Infrastructure & API
<img src="https://img.shields.io/badge/Amazon%20AWS-232F3E?style=for-the-badge&logo=amazon-aws&logoColor=white"> <img src="https://img.shields.io/badge/Amazon%20S3-569A31?style=for-the-badge&logo=Amazon%20S3&logoColor=white"> <img src="https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white"> <img src="https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white"> <img src="https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=slack&logoColor=white">

### Collaboration & Tools
<img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=GitHub&logoColor=white"> <img src="https://img.shields.io/badge/Discord-5865F2?style=for-the-badge&logo=Discord&logoColor=white"> <img src="https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=slack&logoColor=white"> <img src="https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white">

<br>

## [3] Architecture & ERD 

### Architecture
<img width="1434" height="804" alt="image" src="https://github.com/user-attachments/assets/702afd9e-f523-40b0-bd21-4b7035faa5b3" />

### ERD
<img width="1138" height="449" alt="image" src="https://github.com/user-attachments/assets/fb0f2aa1-de19-4a15-bf34-740736b6e8c4" />

## [4] Folder Structure

```bash
haru-api/
├── .github/workflows/    # GitHub Actions CI/CD 파이프라인 (dev, prd 자동 배포)
├── docs/                 # 프로젝트 관련 문서 (README.md, security.md)
├── src/
│   ├── main/java/.../haruapi/
│   │   ├── auth/         # 인증 도메인 (로그인 서비스)
│   │   ├── comment/      # 댓글 도메인 (CRUD 및 비즈니스 로직)
│   │   ├── global/       # 공통 설정 (보안, JWT, S3, Slack 예외 알림, 공통 Entity)
│   │   ├── image/        # 이미지 도메인
│   │   ├── post/         # 게시글 도메인 (AWS S3 연동 게시글 관리)
│   │   └── user/         # 회원 도메인 (회원가입, 정보 수정, 검증)
│   ├── main/resources/   # 환경별 프로필 설정 (application-local/dev/prd/.yml)
│   └── test/             # 도메인별 테스트 코드
├── .env                  # 환경 변수 설정 템플릿
├── build.gradle          # Gradle 의존성 및 빌드 설정
├── docker-compose.yml    # 배포 환경 인프라(DB 등) 구성
└── Dockerfile            # Spring Boot 애플리케이션 도커 이미지 빌드 파일 
```

## [5] Our Team

| Name | Role | GitHub |
| :---: | --- | :---: |
| **박주희(팀장)** | <ul><li>댓글 CRUD 및 예외 처리</li><li>공통 Entity 설계</li><li>AWS 인프라 및 DB 구축</li><li>Docker, CI/CD 배포 환경 구축</li></ul> | [@heeheepark](https://github.com/heeheepark) |
| **금선제** | <ul><li>Spring Security·JWT 인증/인가 구축</li><li>회원가입, 로그인, 로그아웃</li><li>회원정보 관리</li></ul> | [@Aurumm79](https://github.com/Aurumm79) |
| **최형석** | <ul><li>게시글 CRUD 및 예외 처리</li><li>게시글 이미지 관리</li><li>AWS S3 이미지 업로드 연동</li></ul> | [@Hseok-2](https://github.com/Hseok-2) |


