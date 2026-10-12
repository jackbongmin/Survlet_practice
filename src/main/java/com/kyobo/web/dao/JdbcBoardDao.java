package com.kyobo.web.dao;

import com.kyobo.web.config.ConnectionProvider;
import com.kyobo.web.model.BoardPost;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcBoardDao implements BoardDao {

    private BoardPost map(ResultSet rs) throws SQLException {
        BoardPost p = new BoardPost();
        p.setPostId(rs.getLong("post_id"));
        p.setUserId(rs.getString("user_id"));
        p.setAuthorName(rs.getString("author_name"));
        p.setTitle(rs.getString("title"));
        p.setContent(rs.getString("content"));
        p.setImagePath(rs.getString("image_path"));
        p.setLikeCount(rs.getInt("like_count"));
        p.setCommentCount(rs.getInt("comment_count"));

        Timestamp cTs = rs.getTimestamp("created_at");
        if (cTs != null) p.setCreatedAt(cTs.toLocalDateTime());

        Timestamp uTs = rs.getTimestamp("updated_at");
        if (uTs != null) p.setUpdatedAt(uTs.toLocalDateTime());

        return p;
    }

    @Override
    public List<BoardPost> findAll(int limit, int offset) throws SQLException {
        String sql = "SELECT p.post_id, p.user_id, m.name AS author_name, p.title, p.content, "
                + "       p.image_path, p.like_count, p.comment_count, p.created_at, p.updated_at "
                + "FROM board_post p "
                + "JOIN member m ON p.user_id = m.user_id "
                + "ORDER BY p.created_at DESC "
                + "LIMIT ? OFFSET ?";

        List<BoardPost> list = new ArrayList<>();
        try (Connection conn = ConnectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    @Override
    public Optional<BoardPost> findById(Long postId) throws SQLException {
        String sql = "SELECT p.post_id, p.user_id, m.name AS author_name, p.title, p.content, "
                + "       p.image_path, p.like_count, p.comment_count, p.created_at, p.updated_at "
                + "FROM board_post p "
                + "JOIN member m ON p.user_id = m.user_id "
                + "WHERE p.post_id = ?";

        try (Connection conn = ConnectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, postId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public Long save(BoardPost post) throws SQLException {
        String sql = "INSERT INTO board_post (user_id, title, content, image_path) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, post.getUserId());
            ps.setString(2, post.getTitle());
            ps.setString(3, post.getContent());
            ps.setString(4, post.getImagePath());
            ps.executeUpdate();

            try (ResultSet gk = ps.getGeneratedKeys()) {
                if (gk.next()) {
                    return gk.getLong(1);
                }
            }
        }
        throw new SQLException("게시글 번호(PK)를 생성하지 못했습니다.");
    }

    @Override
    public boolean update(BoardPost post) throws SQLException {
        String sql = "UPDATE board_post SET title = ?, content = ?, image_path = ? WHERE post_id = ?";
        try (Connection conn = ConnectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getContent());
            ps.setString(3, post.getImagePath());
            ps.setLong(4, post.getPostId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(Long postId) throws SQLException {
        String sql = "DELETE FROM board_post WHERE post_id = ?";
        try (Connection conn = ConnectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, postId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public void updateLikeCount(Long postId, int delta) throws SQLException {
        String sql = "UPDATE board_post SET like_count = GREATEST(0, like_count + ?) WHERE post_id = ?";
        try (Connection conn = ConnectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setLong(2, postId);
            ps.executeUpdate();
        }
    }

    @Override
    public void updateCommentCount(Long postId, int delta) throws SQLException {
        String sql = "UPDATE board_post SET comment_count = GREATEST(0, comment_count + ?) WHERE post_id = ?";
        try (Connection conn = ConnectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setLong(2, postId);
            ps.executeUpdate();
        }
    }
}