package com.kyobo.web.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
* 세션에 저장될 회원 정보 객체
 * 새션 클러스터링이나 직렬화를 고려하여 Serializable을 구현하는게 좋음
* */
public class Member implements Serializable {
    private static final long  serialVersionUID = 1L;

    private String userId;
    private String password;
    private String name;
    private String email;
    private LocalDateTime createdAt;

    public Member(){

    }
    public Member(String userId, String password, String name) {
        this.userId = userId;
        this.password = password;
        this.name = name;
    }

    public Member(String userId, String name, String email, String password) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Member{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}