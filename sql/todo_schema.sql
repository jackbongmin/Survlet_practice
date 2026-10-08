-- 1. kyobo 데이터베이스 사용 (없으면 생성)
CREATE DATABASE IF NOT EXISTS kyobo
  CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE kyobo;

-- 2. 회원 테이블 (member)
CREATE TABLE IF NOT EXISTS member (
                                      user_id VARCHAR(50) PRIMARY KEY COMMENT '사용자 아이디 (로그인 식별자)',
    password VARCHAR(100) NOT NULL COMMENT '비밀번호',
    name VARCHAR(50) NOT NULL COMMENT '사용자 이름/닉네임',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '가입일'
    );

-- 3. 할 일 테이블 (todo)
CREATE TABLE IF NOT EXISTS todo (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '할 일 고유 번호',
                                    user_id VARCHAR(50) NOT NULL COMMENT '작성자 회원 아이디',
    content VARCHAR(255) NOT NULL COMMENT '할 일 내용',
    is_done BOOLEAN DEFAULT FALSE COMMENT '완료 여부 (0: 미완료, 1: 완료)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
    CONSTRAINT fk_todo_member FOREIGN KEY (user_id) REFERENCES member(user_id) ON DELETE CASCADE
    );

-- 4. 요청하신 테스트 계정 추가 (아이디: 백종민/1234, 김유진/1234)
INSERT INTO member (user_id, password, name)
VALUES
    ('백종민', '1234', '백종민'),
    ('김유진', '1234', '김유진')
    ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 5. 테스트용 초기 할 일 샘플 데이터
INSERT INTO todo (user_id, content, is_done)
VALUES
    ('백종민', 'JSP와 서블릿 개념 복습하기', TRUE),
    ('백종민', 'ConnectionProvider로 MySQL 연결 테스트하기', FALSE),
    ('김유진', 'MySQL Workbench에서 테이블 생성 완료하기', TRUE),
    ('김유진', 'JSTL c:forEach 문법 확인하기', FALSE);