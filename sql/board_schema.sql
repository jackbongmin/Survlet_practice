-- ====================================================================
-- [기존 DB가 이미 구축되어 있는 경우]: 아래 ALTER 문을 실행합니다.
-- ====================================================================
USE kyobo;

-- 1. board_post 테이블에 view_count 컬럼 추가 (기본값: 0)
ALTER TABLE board_post
    ADD COLUMN view_count INT DEFAULT 0 COMMENT '게시글 누적 조회수' AFTER image_path;

-- 2. 기존 게시글들의 조회수를 임의의 테스트 값으로 초기화 (선택 사항)
UPDATE board_post SET view_count = 15 WHERE post_id = 1;
UPDATE board_post SET view_count = 42 WHERE post_id = 2;


-- ====================================================================
-- [DB를 처음부터 새로 생성하는 경우]: 아래의 통합 DDL을 실행합니다.
-- ====================================================================
USE kyobo;

CREATE TABLE IF NOT EXISTS board_post (
                                          post_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '게시글 고유 번호 (PK)',
                                          user_id VARCHAR(50) NOT NULL COMMENT '작성자 회원 아이디 (FK)',
    title VARCHAR(200) NOT NULL COMMENT '게시글 제목',
    content TEXT NOT NULL COMMENT '게시글 본문 내용 (엔터 개행 포함)',
    image_path VARCHAR(255) NULL COMMENT '업로드된 이미지의 웹 경로 (예: /assets/uploads/board/uuid.jpg)',
    view_count INT DEFAULT 0 COMMENT '게시글 누적 조회수 (신규 추가)',
    like_count INT DEFAULT 0 COMMENT '좋아요 누적 수 (조회 성능 최적화 캐시 컬럼)',
    comment_count INT DEFAULT 0 COMMENT '댓글 누적 수 (조회 성능 최적화 캐시 컬럼)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '최초 등록일시',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '최종 수정일시',

    CONSTRAINT fk_board_post_member FOREIGN KEY (user_id) REFERENCES member(user_id) ON DELETE CASCADE,
    INDEX idx_board_post_created (created_at DESC)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='피드 게시글 테이블';