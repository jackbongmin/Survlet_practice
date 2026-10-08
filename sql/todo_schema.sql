-- 1. 데이터베이스 선택 (ConnectionProvider에서 설정한 kyobo DB 사용)
USE kyobo;

-- 2. 회원 테이블 (member)
CREATE TABLE IF NOT EXISTS member (
                                      user_id VARCHAR(50) PRIMARY KEY COMMENT '사용자 아이디 (로그인 식별자)',
    password VARCHAR(100) NOT NULL COMMENT '비밀번호',
    name VARCHAR(50) NOT NULL COMMENT '사용자 이름/닉네임',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '가입일'
    );

-- 3. TODO 리스트 테이블 (todo)
CREATE TABLE IF NOT EXISTS todo (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '할 일 고유 번호',
                                    user_id VARCHAR(50) NOT NULL COMMENT '작성자 회원 아이디',
    content VARCHAR(255) NOT NULL COMMENT '할 일 내용',
    is_done BOOLEAN DEFAULT FALSE COMMENT '완료 여부 (0: 미완료, 1: 완료)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
    CONSTRAINT fk_todo_member FOREIGN KEY (user_id) REFERENCES member(user_id) ON DELETE CASCADE
    );

-- 4. 테스트 계정 추가 (아이디: 백종민 / 비밀번호: 1234, 아이디: 김유진 / 비밀번호: 1234)
INSERT INTO member (user_id, password, name)
VALUES
    ('백종민', '1234', '백종민'),
    ('김유진', '1234', '김유진')
    ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 5. 테스트용 초기 TODO 데이터 삽입 (선택 사항)
INSERT INTO todo (user_id, content, is_done)
VALUES
    ('백종민', '서블릿과 JSP 기초 개념 정리하기', TRUE),
    ('백종민', 'ConnectionProvider로 JDBC 연결 확인하기', FALSE),
    ('김유진', 'MySQL Workbench에서 테이블 생성하기', TRUE),
    ('김유진', 'JSTL c:forEach 문법 복습하기', FALSE);