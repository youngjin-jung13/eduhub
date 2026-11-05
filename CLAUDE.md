# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 프로젝트

EduHub: 임직원이 사내 교육과정을 조회·신청하고, 정원이 차면 대기자로 등록되는 시스템. `docs/requirements.md`에 기능 요구사항(FR-01~09), 비기능 요구사항(NFR-01~08), 결정 사항(D-xx)과 기능별 구현 주차가 정리되어 있다.

현재 상태: `backend/`에 Spring Boot 뼈대만 있다 (1주차). 엔티티와 실제 API는 2주차부터, 프론트엔드(React 18 + TS, Module Federation: shell / course-mfe / enrollment-mfe, npm workspaces)는 아직 없다.

## 팀 규칙

- 요구사항은 `docs/requirements.md`를 기준으로 한다 (결정 사항 D-xx 포함). 작업 범위는 문서의 주차를 따른다.
- 백엔드 빌드·테스트: `cd backend && ./gradlew test`
- 계층 규칙: Controller → Service → Repository. Controller에서 Repository를 직접 호출하지 않는다.
- 엔티티를 API 응답으로 그대로 반환하지 않는다 (DTO 사용).
- 비밀 정보는 코드와 설정 파일에 쓰지 않는다. 환경 변수와 `.env`(커밋 금지)만 사용한다. 필요한 변수 이름은 루트 `.env.example`에 값 없이 추가한다.

## 명령어 (모두 `backend/`에서 실행)

```bash
./gradlew test --tests 'com.example.eduhub.common.api.HealthControllerTest'   # 클래스 하나
./gradlew test --tests '*HealthControllerTest.health_returnsUp'               # 메서드 하나
./gradlew spotlessApply      # 포맷 정리 (google-java-format)
./gradlew spotlessCheck      # 포맷 검사 (check/build에 포함, test에는 미포함)
./gradlew bootRun            # dev 프로필로 실행 — DB_* 환경 변수와 로컬 PostgreSQL 필요
```

- 커버리지 리포트: `build/reports/jacoco/test/html/index.html`. 70% 기준(NFR-05)은 서비스 계층이 생기는 3주차에 건다.
- API 문서: 실행 후 `/v3/api-docs`, `/swagger-ui.html`.
- Spring은 `.env`를 자동으로 읽지 않는다. 셸에서 export하거나 IDE 실행 설정에서 불러온다.

## 로컬 환경 주의

- JDK 21이 필요하다. 시스템 `JAVA_HOME`이 다른 버전이면 `backend/gradle.properties`(gitignore됨, PC마다 각자 작성)에 `org.gradle.java.home=<JDK 21 경로>`를 지정한다.
- Windows에서 TEMP가 8.3 짧은 경로(`~1` 포함)이면 JDK 16+의 AF_UNIX 소켓 생성이 실패한다(`Unable to establish loopback connection`). 해당 JDK의 `conf/net.properties`에 `jdk.net.unixdomain.tmpdir=<긴 이름 경로>`를 지정한다. `org.gradle.jvmargs`의 `-D`는 데몬 시작 후에 전달되므로 효과가 없다.
- 첫 빌드는 의존성 다운로드 때문에 매우 오래 걸릴 수 있다 (25분 이상 걸린 사례 있음). Gradle 명령은 백그라운드로 돌리고 중복 실행하지 않는다.
- test 프로필은 Docker 없이 돌도록 H2(PostgreSQL 모드)를 쓴다.

## 아키텍처

- **도메인 우선 패키지** (`com.example.eduhub`): `course`(과정·회차, FR-01~03), `enrollment`(신청·대기·취소·승급·수료, FR-04~09), `common`(공통: health, OpenAPI 설정 등). 각 도메인 안은 `api`(Controller, DTO) / `service` / `repository` / `domain`(엔티티)으로 나누고, 팀 규칙의 계층 순서를 따른다 (NFR-07).
- **도메인 간 의존은 `enrollment → course` 한쪽뿐**이다. enrollment는 course의 Service를 통해서만 접근하고 course의 Repository를 직접 쓰지 않는다. 규칙은 각 도메인의 `package-info.java`에도 적혀 있다.
- **스키마는 Flyway가 관리한다** (`src/main/resources/db/migration`). JPA는 `ddl-auto: validate`라서 엔티티를 추가할 때 마이그레이션도 함께 추가해야 컨텍스트가 뜬다.
- **프로필**: `application.yml`(공통, 기본 프로필 dev) / `application-dev.yml`(PostgreSQL, 접속 정보는 전부 환경 변수) / `application-test.yml`(H2). 테스트 클래스는 `@ActiveProfiles("test")`를 붙인다.
- **공통 설정의 의도**
  - `open-in-view: false`: N+1 문제를 개발 중에 드러내기 위해 (NFR-02).
  - `hibernate.jdbc.time_zone: Asia/Seoul`: D-01(취소는 회차 시작 1일 전까지) 계산 기준.
- **API 계약 (NFR-04)**: 모든 API는 OpenAPI 문서에 나와야 한다. `OpenApiDocsTest`처럼 새 엔드포인트도 `/v3/api-docs`에 노출되는지 테스트한다. 프론트 클라이언트는 이 스펙에서 생성할 예정이다.

## 테스트에서 주의할 점

- H2는 락과 동시성 동작이 PostgreSQL과 다르다. NFR-01 동시성 테스트(100명이 마지막 1자리에 동시 신청)와 D-02(대기는 정원의 50%까지) 검증은 실제 PostgreSQL에서 해야 한다. 3주차 전에 테스트 DB 전략을 다시 정한다.
