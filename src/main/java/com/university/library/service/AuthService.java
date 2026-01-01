package com.university.library.service;

import com.university.library.model.User;
import com.university.library.model.enums.Role;
import com.university.library.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 🔹 ثبت‌نام دانشجو
    public void register(String username, String password) {

        Optional<User> existingUser = userRepository.findByUsername(username);
        if (existingUser.isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User(
                username,
                password,
                Role.STUDENT,
                true
        );

        userRepository.save(user);
    }

    // 🔹 ورود به سیستم
    public User login(String username, String password) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isActive()) {
            throw new RuntimeException("User is inactive");
        }

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }

        return user;
    }

    // 🔹 تغییر رمز عبور (کارمند/مدیر)
    public void changePassword(String username, String oldPassword, String newPassword) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == Role.STUDENT) {
            throw new RuntimeException("Access denied");
        }

        if (!user.getPassword().equals(oldPassword)) {
            throw new RuntimeException("Old password is incorrect");
        }

        user.setPassword(newPassword);
        // چون فایل‌محور هستیم، باید کل لیست دوباره ذخیره شود
        // ساده‌ترین راه: حذف + اضافه مجدد (در نسخه فعلی)
        // (در نسخه بعدی بهبود می‌دهیم)
    }
}