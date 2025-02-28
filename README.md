# 강연 신청 시스템 개발 명세

## 1. 기술 스택  
### 📌 Backend  
* Java: 17+
* Framework: Spring Boot 3.4.x

### 📌 Database
* MySQL
* QueryDSL

### 📌 Testing  
* JUnit
  
### 📌 Others
* Redis
* Swagger
* JWT

<br/> 

## 2. API 목록

### BackOffice API
1. **강연 목록**  
   - **메소드**: `GET /admin/api/v1/lectures`  
   - **설명**: 전체 강연 목록을 조회합니다.
- **요청 파라미터**:   

| 이름              | 타입              | 필수 여부 | 기본값   | 설명                                                    |
|-------------------|-------------------|--------|--------|-------------------------------------------------------|
| `page`            | `int`             | ❌     | `0`    | 조회할 페이지 번호 (기본값: 0)                                 |
| `size`            | `int`             | ❌     | `10`   | 페이지당 데이터 개수 (기본값: 10)                             |
| `sort`            | `LectureSort`     | ❌     | `CREATED_AT` | 정렬 기준 (`CREATED_AT`, `CAPACITY`, `CURRENT_CAPACITY`, `START_TIME` )     |
| `sortDirection`   | `SortDirection`   | ❌     | `DESC` | 정렬 방향 (`DESC`, `ASC`)                        |


- **응답 예시**:
   ```json
   {
       "code": "SUCCESS",
       "content": {
           "totalPages": 1,
           "isLast": true,
           "totalElements": 10,
           "lectureList": [
               {
                   "id": 10,
                   "lecturer": "최수빈",
                   "location": "인천 부평",
                   "capacity": 50,
                   "currentCapacity": 20,
                   "startTime": "2025-03-28T19:00:00",
                   "content": "스타트업 창업 전략"
               },
               {
                   "id": 9,
                   "lecturer": "박서연",
                   "location": "서울 송파",
                   "capacity": 100,
                   "currentCapacity": 80,
                   "startTime": "2025-03-25T18:00:00",
                   "content": "빅데이터 분석과 활용"
               },
             ...
           ]
       }
   }
   ```

2. **강연 등록**  
   - **메소드**: `POST /admin/api/v1/lectures`  
   - **설명**: 강연자, 강연장, 신청 인원, 강연 시간, 강연 내용을 입력하여 새로운 강연을 등록합니다.
   - **요청 바디**:
    ```json 
       {
       "lecturer": "박다솔",
       "location": "경기 화성",
       "capacity": 6,
       "startTime": "2025-06-01 10:00:00",
       "content": "체어 + 바렐 : 강의실에 오시면 당일 진행 방식(체어 또는 바렐)을 안내해 드립니다."
       }
     ```

3. **강연 신청자 목록**  
   - **메소드**: `GET /admin/api/v1/lectureId/{lecture_id}/applications`  
   - **설명**: 특정 강연에 신청한 사번 목록을 조회합니다.
   - **요청 파라미터**:

| 이름              | 타입    | 필수 여부 | 기본값   | 설명                                         |
|-------------------|---------|--------|--------|--------------------------------------------|
| `lectureId`       | `Long`  | 필수     | 없음     | 강연 ID (경로 변수, 요청한 강연을 식별)             |
| `page`            | `int`   | ❌     | `0`     | 조회할 페이지 번호 (기본값: 0)                       |
| `size`            | `int`   | ❌     | `10`    | 페이지당 데이터 개수 (기본값: 10)                    |

   - **응답 예시**:
   ```json
   {
    "code": "SUCCESS",
    "content": {
        "totalPages": 1,
        "isLast": true,
        "totalElements": 4,
        "applicantUserList": [
            {
                "employeeNo": "45678",
                "applicationAt": "2025-02-27T16:05:46"
            },
            {
                "employeeNo": "34567",
                "applicationAt": "2025-02-27T16:02:32"
            },
            {
                "employeeNo": "23456",
                "applicationAt": "2025-02-27T15:55:08"
            },
            {
                "employeeNo": "12345",
                "applicationAt": "2025-02-27T15:55:08"
            }
            ]
        }
    }
   ```

### Front API
1. **강연 목록**  
   - **메소드**: `GET /api/v1/lectures/available`  
   - **설명**: 강연 시작 시간이 `강연 시작 시간 + 1일 >= 현재 시각` 인  강연들을 노출합니다.
   - **요청 파라미터**:

| 이름            | 타입    | 필수 여부 | 기본값   | 설명                                                      |
|---------------|-------|--------|--------|---------------------------------------------------------|
| `page`       | `int` | ❌     | `0`    | 조회할 페이지 번호 (0부터 시작)                                     |
| `size`       | `int` | ❌     | `10`   | 한 페이지에 포함할 강연 개수                                        |
| `sort`            | `LectureSort`     | ❌     | `CREATED_AT` | 정렬 기준 (`CREATED_AT`, `CAPACITY`, `CURRENT_CAPACITY`, `START_TIME` )     |
| `sortDirection`   | `SortDirection`   | ❌     | `DESC` | 정렬 방향 (`DESC`, `ASC`)                        |

   - **응답 예시**:
   ```json 
   {
    "code": "SUCCESS",
    "content": {
        "totalPages": 1,
        "isLast": true,
        "totalElements": 4,
        "lectureList": [
            {
                "id": 10,
                "lecturer": "최수빈",
                "location": "인천 부평",
                "capacity": 50,
                "currentCapacity": 20,
                "startTime": "2025-02-27T23:30:00",
                "content": "스타트업 창업 전략"
            },
            {
                "id": 9,
                "lecturer": "박서연",
                "location": "서울 송파",
                "capacity": 100,
                "currentCapacity": 80,
                "startTime": "2025-02-25T18:00:00",
                "content": "빅데이터 분석과 활용"
            },
            ...
            ]
      }
   }
   ```

2. **강연 신청**  
   - **메소드**: `POST /api/v1/lectures/{lectureId}/applications`  
   - **설명**: 사번을 입력 받아 신청을 받습니다. 같은 강연에 중복 신청은 불가능합니다.
   - **요청 바디**:
   ```json
   {
    "employeeNo" : "12345"
   }
   ```

3. **신청 내역 조회**  
   - **메소드**: `GET /api/v1/lectures/applications`  
   - **설명**: 입력된 사번으로 신청한 강연 목록을 조회합니다. (강연 시작 시간이 1주일 전 ~ 1일 후인 강연들을 노출합니다.)
   - **요청 파라미터**:

| 이름              | 타입                     | 필수 여부 | 기본값       | 설명                                                   |
|-------------------|--------------------------|--------|------------|------------------------------------------------------|
| `page`           | `int`                     | 선택     | `0`        | 조회할 페이지 번호 (기본값: 0)                                  |
| `size`           | `int`                     | 선택     | `10`       | 페이지당 데이터 개수 (기본값: 10)                               |
| `employeeNo`     | `String`                  | 필수     | 없음        | 사용자의 사번 (신청한 강연을 조회하기 위한 식별 값)                     |
| `sort`           | `LectureApplicationSort`  | 선택     | `CREATED_AT` | 정렬 기준 (`CAPACITY`, `CURRENT_CAPACITY`, `START_TIME`, `CREATED_AT`) |
| `sortDirection`  | `SortDirection`           | 선택     | `DESC`      | 정렬 방향 (`DESC`, `ASC`)                      |
    
   - **응답 예시**:
   ```json
   {
    "code": "SUCCESS",
    "content": {
        "totalPages": 3,
        "isLast": false,
        "totalElements": 30,
        "lectureApplicationList": [
            {
                "id": 3,
                "lecturer": "이영희",
                "location": "서울 강북",
                "capacity": 120,
                "currentCapacity": 70,
                "startTime": "2025-02-28T11:00:00",
                "content": "프로그래밍 언어 비교",
                "applicationId": 8
            },
            {
                "id": 2,
                "lecturer": "김철수",
                "location": "서울 종로",
                "capacity": 80,
                "currentCapacity": 30,
                "startTime": "2025-02-27T11:00:00",
                "content": "데이터 분석의 핵심",
                "applicationId": 7
            },
            {
                "id": 1,
                "lecturer": "홍길동",
                "location": "서울 강남",
                "capacity": 100,
                "currentCapacity": 50,
                "startTime": "2025-02-26T19:00:00",
                "content": "AI 기술의 발전과 미래",
                "applicationId": 6
            }
            ]
        }
    }
   ```

4. **신청한 강연 취소**  
   - **메소드**: `DELETE /api/v1/lectures/{lectureId}/applications/{applicationId}`  
   - **설명**: 강연 신청을 취소합니다.
   - **응답 예시**:
   ```json
   {
    "code": "SUCCESS",
    "content": "SUCCESS"
   }
   ```

5. **기간 내 인기 강연**  
   - **메소드**: `GET /api/v1/lectures/popular`  
   - **설명**: 최근 n일간 가장 신청이 많은 강연 순으로 조회합니다.
   - **요청 파라미터**:

| 이름          | 타입  | 필수 여부 | 기본값 | 설명                                      |
|--------------|------|--------|------|-----------------------------------------|
| `page`       | `int` | 선택   | `0`  | 조회할 페이지 번호 (기본값: 0)               |
| `size`       | `int` | 선택   | `10` | 페이지당 데이터 개수 (기본값: 10)            |
| `periodDays` | `int` | 선택   | `3`  | 최근 n일 동안 가장 신청이 많은 강연 조회 (기본값: 3일) |
  
   - **응답 예시**:
   ```json
   {
    "code": "SUCCESS",
    "content": {
        "totalPages": 2,
        "isLast": false,
        "totalElements": 15,
        "lectureList": [
            {
                "id": 10,
                "lecturer": "최수빈",
                "location": "인천 부평",
                "capacity": 50,
                "currentCapacity": 20,
                "startTime": "2025-03-01T23:30:00",
                "content": "스타트업 창업 전략"
            },
            {
                "id": 1,
                "lecturer": "홍길동",
                "location": "서울 강남",
                "capacity": 100,
                "currentCapacity": 50,
                "startTime": "2025-02-26T19:00:00",
                "content": "AI 기술의 발전과 미래"
            },
          ...
            ]
        }
    }
   ```


## 3. 테이블 구성

### 사용자 테이블 (users)
| 컬럼 이름       | 데이터 타입                         | 설명            |
|----------------|--------------------------------|-----------------|
| id             | INT                            | 사용자 고유 ID   |
| employee_no    | CHAR(5)                        | 사번 (5자리)     |
| password       | VARCHAR(255)                   | 비밀번호         |
| role           | ENUM('ROLE_ADMIN','ROLE_USER') | 권한 (admin/user) |
| created_at     | DATETIME                       | 데이터 생성 시간   |
| updated_at     | DATETIME                       | 데이터 수정 시간   |

```sql
INSERT INTO users (employee_no, password, role, created_at, updated_at)
VALUES
('12345', '$2b$12$L1KmQVskTrCK1hYbMKQ3Y.Ikox7Kqe3KIs7xxLrF71OTq4wXItBz.', 'ROLE_ADMIN', NOW(), NOW()),
('23456', '$2a$10$3Z2m6F2FZXFJk7zN.e2RjtXQAkntXvn0jv5q0k4hDQdhz2M9r.Ii.', 'ROLE_USER', NOW(), NOW()),
('34567', '$2a$10$KMGyHJpt0XnRmZCuPrlL8KH2FGnX6vYmjpAA1qHG2X7VccXW8PQPa', 'ROLE_USER', NOW(), NOW()),
('45678', '$2a$10$7tpIjgA7l8byhpd7VvqosXtCxk5HHRqa0CH7KGG67PUXZPL.iyjWq', 'ROLE_ADMIN', NOW(), NOW()),
('56789', '$2a$10$gNYs6tUrLlOrkp5U.ZYmnVnKg2TsfSaHTuAOPxl0XB0iNTfDRwQoa', 'ROLE_USER', NOW(), NOW());
```

### 강연 테이블 (lectures)
| 컬럼 이름       | 데이터 타입   | 설명              |
|----------------|--------------|-------------------|
| id             | INT          | 강연 고유 ID       |
| lecturer       | VARCHAR(100) | 강연자             |
| location       | VARCHAR(255) | 강연장             |
| capacity       | INT          | 신청 가능 인원수   |
| current_capacity | INT        | 현재 신청 인원수   |
| start_time     | DATETIME     | 강연 시작 시간     |
| content        | TEXT         | 강연 내용          |
| created_at     | DATETIME     | 데이터 생성 시간   |
| updated_at     | DATETIME     | 데이터 수정 시간   |


### 신청자 테이블 (applications)
| 컬럼 이름       | 데이터 타입   | 설명              |
|----------------|--------------|-------------------|
| id             | INT          | 신청 고유 ID       |
| lecture_id     | INT          | 강연 ID            |
| employee_no    | CHAR(5)      | 사번 (5자리)       |
| created_at     | DATETIME     | 데이터 생성 시간   |
| updated_at     | DATETIME     | 데이터 수정 시간   |

## 4. 강연 신청 처리 흐름

### 1. 사용자가 강연을 신청
사용자가 강연에 신청을 하면, 다음의 조건을 확인합니다.

### 2. 신청 조건 확인

- 2-1. 강연 시작 시간이 현재 시간보다 전인지 : 강연의 시작 시간이 현재 시간보다 미래라면 신청이 가능합니다. 현재 시간보다 이전이면 신청할 수 없습니다.
- 2-2. 신청 가능한 자리가 남았는지 : 강연에 남은 좌석이 있는지 확인합니다. 좌석이 모두 찼다면 신청을 받을 수 없습니다.
- 2-3. 중복 신청인지 : 해당 사용자가 이미 신청한 강연인지 확인합니다. 같은 강연에 중복으로 신청할 수 없도록 합니다.

### 3. 신청 내역 저장
 
- 3-1. Redis에 신청 내역 저장 : 강연 신청이 유효하다면, 해당 신청 정보를 Redis에 저장합니다. 저장되는 키 값은 `"lecture:" + lectureId + ":applications:new"`로, 사용자의 사번을 Redis의 Set 자료형에 추가합니다.
- 3-2. 스케줄러로 Redis 데이터 DB에 저장 : Redis에 저장된 신청 내역을 주기적으로 DB에 반영합니다. 이를 위해 스케줄러가 1분마다 실행되어, Redis에 저장된 lecture:*:new 형식의 키들을 확인하고, 그 안에 포함된 신청 내역을 DB에 저장합니다.
  1. 모든 lecture:*:new 형식의 키를 가져옵니다.
  2. Redis에 저장된 신청 내역을 DB에 저장
  3. 신청 내역을 기존 신청 목록으로 이동 : Redis에서 신청자가 추가된 `lecture:*:new` 의 신청 내역을 기존 신청 목록인 `lecture:*:applications` 로 이동시킵니다.

  
