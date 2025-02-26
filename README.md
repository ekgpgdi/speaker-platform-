# 강연 신청 시스템 개발 명세

## 1. API 목록

### BackOffice API
1. **강연 목록**  
   - **메소드**: `GET /admin/api/v1/lectures`  
   - **설명**: 전체 강연 목록을 조회합니다.
   - **요청 파라미터**:   
   - **응답 예시**:

2. **강연 등록**  
   - **메소드**: `POST /admin/api/v1/lectures`  
   - **설명**: 강연자, 강연장, 신청 인원, 강연 시간, 강연 내용을 입력하여 새로운 강연을 등록합니다.
   - **요청 바디**:
    ```json 
       {
       "lecturer": "박다솔",
       "location": "경기도 화성시 남여울2길 4",
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
   - **응답 예시**:

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

### 신청자 테이블 (applications)
| 컬럼 이름       | 데이터 타입   | 설명              |
|----------------|--------------|-------------------|
| id             | INT          | 신청 고유 ID       |
| lecture_id     | INT          | 강연 ID            |
| employee_no    | CHAR(5)      | 사번 (5자리)       |
| created_at     | DATETIME     | 데이터 생성 시간   |
| updated_at     | DATETIME     | 데이터 수정 시간   |
