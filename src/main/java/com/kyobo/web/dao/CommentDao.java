package com.kyobo.web.dao;

import com.kyobo.web.model.BoardComment;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CommentDao {
    // 1. 단일 게시글의 댓글 목록 조회
    List<BoardComment> findByPostId(Long postId) throws SQLException;

    // 2. [N+1 방지 핵심] 여러 게시글들의 댓글들을 한 번에 조회하여 postId별로 묶어 반환
    Map<Long, List<BoardComment>> findByPostIds(List<Long> postIds) throws SQLException;

    // 3. 댓글 1건 상세 조회
    Optional<BoardComment> findById(Long commentId) throws SQLException;

    // 4. 신규 댓글 저장
    Long save(BoardComment comment) throws SQLException;

    // 5. 댓글 삭제
    boolean delete(Long commentId) throws SQLException;
}