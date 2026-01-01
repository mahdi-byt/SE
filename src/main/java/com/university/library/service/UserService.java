package com.university.library.service;

import com.university.library.model.User;
import com.university.library.repository.UserRepository;
import com.university.library.repository.wrapper.UsersWrapper;
import com.university.library.util.JsonFileUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 🔹 گرفتن کاربر با username
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // 🔹 تغییر وضعیت فعال/غیرفعال
    public void setUserActiveStatus(String username, boolean isActive) {
        var users = userRepository.findAll();
        users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .ifPresent(u -> u.setActive(isActive));
        // ذخیره کل لیست بعد از تغییر
        userRepository.saveAll(users);
    }

    // 🔹 گرفتن همه کاربران (در صورت نیاز)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> findAll() {
        return userRepository.findAll(); // فرض می‌کنیم userRepository متد findAll() دارد
    }

    public void saveAll(List<User> users) {
        UsersWrapper wrapper = new UsersWrapper(); // کلاس کمکی برای نگهداری List<User>
        wrapper.setUsers(users);
        JsonFileUtil.writeToFile("users.json", wrapper);
    }

    public void addUser(User user) {
        List<User> users = findAll();
        users.add(user);
        saveAll(users);
    }
}