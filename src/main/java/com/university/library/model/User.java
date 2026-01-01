package com.university.library.model;

import com.university.library.model.enums.Role;

public class User {

    private String username;
    private String password;
    private Role role;
    private boolean isActive;

    // ✅ سازنده بدون آرگومان (ضروری برای Jackson)
    public User() {
    }

    // ✅ سازنده کامل
    public User(String username, String password, Role role, boolean isActive) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.isActive = isActive;
    }

    // 🔹 Getter و Setter ها

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    // در نسخه‌های بعدی: رمزنگاری
    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}