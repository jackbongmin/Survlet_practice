package com.kyobo.web.dao;

import com.kyobo.web.config.ConnectionProvider;
import com.kyobo.web.model.TodoItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcTodoDao implements TodoDao {

    // ResultSet에서 TodoItem 객체로 매핑하는 헬퍼 메서드
    private TodoItem map(ResultSet rs) throws SQLException {
        TodoItem item = new TodoItem();
        item.setId(rs.getLong("id"));
        item.setUserId(rs.getString("user_id"));
        item.setContent(rs.getString("content"));
        item.setDone(rs.getBoolean("is_done"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            item.setCreatedAt(ts.toLocalDateTime());
        }
        return item;
    }

    @Override
    public List<TodoItem> findByUserId(String userId) throws SQLException {
        // WHERE user_id = ? : 오직 로그인한 회원 본인의 할 일만 최신순(ORDER BY id DESC)으로 가져옴!
        String sql = "SELECT id, user_id, content, is_done, created_at FROM todo WHERE user_id = ? ORDER BY id DESC";
        List<TodoItem> list = new ArrayList<>();

        try (Connection conn = ConnectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs)); // 여러 줄의 결과를 리스트에 차곡차곡 담음
                }
            }
        }
        return list;
    }

    @Override
    public long insert(TodoItem item) throws SQLException {
        String sql = "INSERT INTO todo (user_id, content, is_done) VALUES (?, ?, ?)";

        // Statement.RETURN_GENERATED_KEYS: DB가 자동 생성한 AUTO_INCREMENT 번호(id)를 돌려받겠다는 옵션
        try (Connection conn = ConnectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, item.getUserId());
            ps.setString(2, item.getContent());
            ps.setBoolean(3, item.isDone());
            ps.executeUpdate(); // INSERT 실행

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1); // 새로 생성된 할 일의 id 반환
                }
            }
        }
        return 0;
    }

    @Override
    public boolean toggleDone(long id, String userId) throws SQLException {
        // [보안 핵심] WHERE id = ? AND user_id = ?
        // 남의 할 일 id를 넘겨도 본인(user_id)이 아니면 절대 수정되지 않도록 방어.
        // is_done = NOT is_done : 현재 값이 TRUE면 FALSE로, FALSE면 TRUE로 반전
        String sql = "UPDATE todo SET is_done = NOT is_done WHERE id = ? AND user_id = ?";

        try (Connection conn = ConnectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.setString(2, userId);
            // executeUpdate()의 결과가 1이면 정확히 1건이 수정되었음을 의미
            return ps.executeUpdate() == 1;
        }
    }

    @Override
    public boolean delete(long id, String userId) throws SQLException {
        // [보안 핵심] WHERE id = ? AND user_id = ?
        // 남의 할 일 id를 넘겨도 본인이 아니면 삭제되지 않음.
        String sql = "DELETE FROM todo WHERE id = ? AND user_id = ?";

        try (Connection conn = ConnectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.setString(2, userId);
            return ps.executeUpdate() == 1;
        }
    }
}