package com.kyobo.web.dao;

import com.kyobo.web.config.ConnectionProvider;
import com.kyobo.web.model.BoardComment;

import java.sql.*;
import java.util.*;

public class JdbcCommentDao implements CommentDao {

    private BoardComment map(ResultSet rs) throws SQLException {
        BoardComment c = new BoardComment();
        c.setCommentId(rs.getLong("comment_id"));
        c.setPostId(rs.getLong("post_id"));
        c.setUserId(rs.getString("user_id"));
        c.setAuthorName(rs.getString("author_name"));
        c.setContent(rs.getString("content"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) c.setCreatedAt(rs.getTimestamp("created_at"));
        return c;
    }

    @Override
    public List<BoardComment> findByPostId(Long postId) throws SQLException {
        String sql = "SELECT c.comment_id, c.post_id, c.user_id, m.name AS author_name, c.content, c.created_at "
                + "FROM board_comment c "
                + "JOIN member m ON c.user_id = m.user_id "
                + "WHERE c.post_id = ? "
                + "ORDER BY c.created_at ASC";

        List<BoardComment> list = new ArrayList<>();
        try (Connection conn = ConnectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, postId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    @Override
    public Map<Long, List<BoardComment>> findByPostIds(List<Long> postIds) throws SQLException {
        Map<Long, List<BoardComment>> result = new HashMap<>();
        if (postIds == null || postIds.isEmpty()) {
            return result;
        }

        // 동적으로 IN (?, ?, ?) 플레이스홀더 문자열 생성
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < postIds.size(); i++) {
            if (i > 0) placeholders.append(",");
            placeholders.append("?");
        }

        String sql = "SELECT c.comment_id, c.post_id, c.user_id, m.name AS author_name, c.content, c.created_at "
                + "FROM board_comment c "
                + "JOIN member m ON c.user_id = m.user_id "
                + "WHERE c.post_id IN (" + placeholders + ") "
                + "ORDER BY c.created_at ASC";

        try (Connection conn = ConnectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < postIds.size(); i++) {
                ps.setLong(i + 1, postIds.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BoardComment c = map(rs);
                    result.computeIfAbsent(c.getPostId(), k -> new ArrayList<>()).add(c);
                }
            }
        }
        return result;
    }

    @Override
    public Optional<BoardComment> findById(Long commentId) throws SQLException {
        String sql = "SELECT c.comment_id, c.post_id, c.user_id, m.name AS author_name, c.content, c.created_at "
                + "FROM board_comment c "
                + "JOIN member m ON c.user_id = m.user_id "
                + "WHERE c.comment_id = ?";

        try (Connection conn = ConnectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, commentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public Long save(BoardComment comment) throws SQLException {
        String sql = "INSERT INTO board_comment (post_id, user_id, content) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, comment.getPostId());
            ps.setString(2, comment.getUserId());
            ps.setString(3, comment.getContent());
            ps.executeUpdate();

            try (ResultSet gk = ps.getGeneratedKeys()) {
                if (gk.next()) return gk.getLong(1);
            }
        }
        throw new SQLException("댓글 번호(PK)를 생성하지 못했습니다.");
    }

    @Override
    public boolean delete(Long commentId) throws SQLException {
        String sql = "DELETE FROM board_comment WHERE comment_id = ?";
        try (Connection conn = ConnectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, commentId);
            return ps.executeUpdate() > 0;
        }
    }
}