package com.university.library.controller.admin;

import com.university.library.dto.ApiResponse;
import com.university.library.model.User;
import com.university.library.model.enums.Role;
import com.university.library.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/employees")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    // 🔹 ایجاد حساب کارمند جدید
    @PostMapping
    public ResponseEntity<ApiResponse> createEmployee(
            @RequestHeader("role") Role role,
            @RequestBody User employee
    ) {
        if (role != Role.ADMIN) {
            throw new RuntimeException("Access denied");
        }

        if (employee.getRole() != Role.EMPLOYEE) {
            throw new RuntimeException("Role must be EMPLOYEE");
        }

        userService.addUser(employee); // UserService باید متد addUser داشته باشد
        return ResponseEntity.ok(new ApiResponse(true, "Employee account created"));
    }

    // 🔹 لیست همه کارمندان
    @GetMapping
    public ResponseEntity<List<User>> getEmployees(
            @RequestHeader("role") Role role
    ) {
        if (role != Role.ADMIN) {
            throw new RuntimeException("Access denied");
        }

        List<User> employees = userService.getAllUsers().stream()
                .filter(u -> u.getRole() == Role.EMPLOYEE)
                .toList();

        return ResponseEntity.ok(employees);
    }
}