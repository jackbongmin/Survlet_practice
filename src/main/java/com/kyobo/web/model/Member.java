package com.kyobo.web.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 로그인한 사용자 정보를 담는 DTO 클래스입니다.
 * 세션(HttpSession)에 저장되므로 Serializable 인터페이스를 구현하는 것이 안전합니다.
 */
public class Member implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;           // 사용자 아이디 (로그인 식별자)
    private String password;         // 비밀번호 (DB 인증용)
    private String name;             // 사용자 이름/닉네임 (화면 표시용)
    private String email;            // 이메일 (기존 호환용)
    private LocalDateTime createdAt; // 가입 일시

    // 기본 생성자 (자바빈즈 규약)
    public Member() {}

    // 필수 정보(아이디, 비밀번호, 이름)로 객체를 생성하는 생성자
    public Member(String userId, String password, String name) {
        this.userId = userId;
        this.password = password;
        this.name = name;
    }

    // 기존 로그인 가이드 호환용 전체 생성자
    public Member(String userId, String name, String email, String password) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // --- Getter & Setter 메서드들 ---
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Member{" + "userId='" + userId + '\'' + ", name='" + name + '\'' + '}';
    }
}