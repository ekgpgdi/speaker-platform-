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
| 컬럼 이름       | 데이터 타입   | 설명            |
|----------------|--------------|-----------------|
| id             | INT          | 사용자 고유 ID   |
| employee_no    | CHAR(5)      | 사번 (5자리)     |
| password       | VARCHAR(50) | 비밀번호         |
| role           | ENUM('ADMIN','USER') | 권한 (admin/user) |
| created_at     | DATETIME     | 데이터 생성 시간   |
| updated_at     | DATETIME     | 데이터 수정 시간   |

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
