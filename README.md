# 강연 신청 시스템 개발 명세

## 1. API 목록

### BackOffice API
1. **강연 목록**  
   - **메소드**: `GET /admin/api/v1/lectures`  
   - **설명**: 전체 강연 목록을 조회합니다.
- **요청 파라미터**:   

| 이름            | 타입    | 필수 여부 | 기본값   | 설명                                                      |
|---------------|-------|--------|--------|---------------------------------------------------------|
| `page`       | `int` | ❌     | `0`    | 조회할 페이지 번호 (0부터 시작)                                     |
| `size`       | `int` | ❌     | `10`   | 한 페이지에 포함할 강연 개수                                        |
| `sort`       | `string` | ❌  | `CREATED_AT` | 정렬 기준 (`CAPACITY`, `CURRENT_CAPACITY`, `START_TIME`, `CREATED_AT` ) |
| `sortDirection` | `string` | ❌ | `DESC`  | 정렬 방향 (`ASC` / `DESC`)                                  |

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
   - **메소드**: `GET /admin/api/v1/lectures/{lecture_id}/applications`  
   - **설명**: 특정 강연에 신청한 사번 목록을 조회합니다.
   - **요청 파라미터**: `lecture_id`  
   - **응답 예시**:

### Front API
1. **강연 목록**  
   - **메소드**: `GET /api/v1/lectures/available`  
   - **설명**: 강연 시작 시간이 1주일 전 ~ 1일 후인 강연들을 노출합니다.
   - **요청 파라미터**:

| 이름            | 타입    | 필수 여부 | 기본값   | 설명                                                      |
|---------------|-------|--------|--------|---------------------------------------------------------|
| `page`       | `int` | ❌     | `0`    | 조회할 페이지 번호 (0부터 시작)                                     |
| `size`       | `int` | ❌     | `10`   | 한 페이지에 포함할 강연 개수                                        |
| `sort`       | `string` | ❌  | `CREATED_AT` | 정렬 기준 (`CAPACITY`, `CURRENT_CAPACITY`, `START_TIME`, `CREATED_AT` ) |
| `sortDirection` | `string` | ❌ | `DESC`  | 정렬 방향 (`ASC` / `DESC`)                                  |

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

3. **신청 내역 조회**  
   - **메소드**: `GET /api/v1/lectures/applications`  
   - **설명**: 입력된 사번으로 신청한 강연 목록을 조회합니다.
   - **요청 파라미터**: `employee_number`  
   - **응답 예시**:
    

4. **신청한 강연 취소**  
   - **메소드**: `DELETE /api/v1/lectures/{lectureId}/applications/{applicationId}`  
   - **설명**: 강연 신청을 취소합니다.
   - **응답 예시**:

5. **실시간 인기 강연**  
   - **메소드**: `GET /api/v1/lectures/popular`  
   - **설명**: 최근 3일간 가장 신청이 많은 강연 순으로 조회합니다.
   - **요청 파라미터**: `period-days=3`
   - **응답 예시**:


## 2. 테이블 구성

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

```sql
INSERT INTO lectures (id, lecturer, location, capacity, current_capacity, start_time, content, created_at, updated_at)
VALUES
(1, '홍길동', '서울 강남', 100, 50, '2025-02-01 10:00:00', 'AI 기술의 발전과 미래', NOW(), NOW()),
(2, '김철수', '서울 종로', 80, 30, '2025-02-05 14:00:00', '데이터 분석의 핵심', NOW(), NOW()),
(3, '이영희', '서울 강북', 120, 70, '2025-02-10 09:00:00', '프로그래밍 언어 비교', NOW(), NOW()),
(4, '박지민', '부산 해운대', 200, 150, '2025-02-12 13:00:00', '클라우드 컴퓨팅의 이해', NOW(), NOW()),
(5, '최민수', '대구 수성구', 60, 25, '2025-02-15 11:00:00', '디지털 마케팅 전략', NOW(), NOW()),
(6, '정하늘', '서울 서초', 90, 60, '2025-02-18 15:00:00', '모바일 개발의 최신 동향', NOW(), NOW()),
(7, '김미영', '경기 성남', 150, 100, '2025-02-20 16:00:00', 'UX/UI 디자인 기초', NOW(), NOW()),
(8, '이상호', '서울 마포', 70, 40, '2025-02-22 17:00:00', '데이터베이스 설계의 기초', NOW(), NOW()),
(9, '박서연', '서울 송파', 100, 80, '2025-02-25 18:00:00', '빅데이터 분석과 활용', NOW(), NOW()),
(10, '최수빈', '인천 부평', 50, 20, '2025-02-27 23:30:00', '스타트업 창업 전략', NOW(), NOW());
```
### 신청자 테이블 (applications)
| 컬럼 이름       | 데이터 타입   | 설명              |
|----------------|--------------|-------------------|
| id             | INT          | 신청 고유 ID       |
| lecture_id     | INT          | 강연 ID            |
| employee_no    | CHAR(5)      | 사번 (5자리)       |
| created_at     | DATETIME     | 데이터 생성 시간   |
| updated_at     | DATETIME     | 데이터 수정 시간   |
