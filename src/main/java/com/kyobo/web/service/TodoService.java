package com.kyobo.web.service;

import com.kyobo.web.dao.TodoDao;
import com.kyobo.web.model.TodoItem;

import java.util.List;
public class TodoService {

    private final TodoDao todoDao;

    public TodoService(TodoDao todoDao){
        this.todoDao = todoDao;
    }

    // 본인 할 일 목록 가져오기
    public List<TodoItem> getTodoList(String userId) throws Exception {
        return todoDao.findByUserId(userId);
    }

    // 새 할 일 등록하기(내용이 비어있는지 검사)
    public long addTodo(String userId, String content) throws Exception {
        if(content == null || content.isBlank()) {
            throw new IllegalArgumentException("할 일 내용을 입력해주세요.");
        }
        TodoItem = new TodoItem(userId, content.trim());
        return todoDao.insert(item);
    }

    // 완료 여부 상태 변경
    public void toggleStatus(long id, String userId) throws Exception {
        boolean updated = todoDao.toggleDone(id, userId);
        if (!updated) {
            throw new IllegalStateException("해당 할 일을 찾을 수 없거나 권한이 없습니다.");
        }
    }

    // 할 일 삭제
    public void removeTodo(long id, String userId) throws Exception {
        boolean deleted = todoDao.delete(id, userId);
        if (!deleted) {
            throw new IllegalStateException("해당 할 일을 찾을 수 없거나 권한이 없습니다.");
        }
    }

}

