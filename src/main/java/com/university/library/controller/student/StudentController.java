package com.university.library.controller.student;

import com.university.library.dto.ApiResponse;
import com.university.library.model.Borrow;
import com.university.library.model.User;
import com.university.library.model.enums.Role;
import com.university.library.service.BorrowService;
import com.university.library.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final UserService userService;
    private final BorrowService borrowService;

    public StudentController(UserService userService, BorrowService borrowService) {
        this.userService = userService;
        this.borrowService = borrowService;
    }

    // 🔹 دریافت پروفایل دانشجو
    @GetMapping("/{username}")
    public ResponseEntity<User> getStudentProfile(
            @PathVariable String username,
            @RequestHeader("username") String currentUsername,
            @RequestHeader("role") Role role
    ) {
        if (role == Role.STUDENT && !currentUsername.equals(username)) {
            throw new RuntimeException("Access denied");
        }

        User user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    // 🔹 مشاهده تاریخچه امانت‌های دانشجو (کارمند/مدیر)
    @GetMapping("/{username}/borrow-history")
    public ResponseEntity<List<Borrow>> getBorrowHistory(
            @PathVariable String username,
            @RequestHeader("role") Role role
    ) {
        if (role == Role.STUDENT) {
            throw new RuntimeException("Access denied");
        }

        List<Borrow> history = borrowService.getBorrowHistoryByStudent(username);
        return ResponseEntity.ok(history);
    }

    // 🔹 فعال/غیرفعال کردن دانشجو (کارمند)
    @PostMapping("/{username}/status")
    public ResponseEntity<ApiResponse> changeStudentStatus(
            @PathVariable String username,
            @RequestHeader("role") Role role,
            @RequestParam boolean isActive
    ) {
        if (role != Role.EMPLOYEE) {
            throw new RuntimeException("Access denied");
        }

        userService.setUserActiveStatus(username, isActive);
        return ResponseEntity.ok(new ApiResponse(true, "Student status updated"));
    }
}