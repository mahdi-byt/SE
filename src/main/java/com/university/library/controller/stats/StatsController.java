package com.university.library.controller.stats;

import com.university.library.dto.ApiResponse;
import com.university.library.model.Borrow;
import com.university.library.model.User;
import com.university.library.model.enums.Role;
import com.university.library.service.BorrowService;
import com.university.library.service.UserService;
import com.university.library.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final UserService userService;
    private final BookService bookService;
    private final BorrowService borrowService;

    public StatsController(UserService userService, BookService bookService, BorrowService borrowService) {
        this.userService = userService;
        this.bookService = bookService;
        this.borrowService = borrowService;
    }

    // 🔹 آمار خلاصه (تعداد دانشجو، کتاب، امانت)
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {

        Map<String, Object> summary = new HashMap<>();
        long students = userService.getAllUsers().stream()
                .filter(u -> u.getRole() == Role.STUDENT)
                .count();
        long books = bookService.getBooks(null, null, null).size();
        long borrows = borrowService.getAllRequests().size();

        summary.put("studentsCount", students);
        summary.put("booksCount", books);
        summary.put("borrowsCount", borrows);

        return ResponseEntity.ok(summary);
    }

    // 🔹 آمار پیشرفته امانت‌ها (مدیر)
    @GetMapping("/borrows")
    public ResponseEntity<Map<String, Object>> getBorrowStats(
            @RequestHeader("role") Role role
    ) {
        if (role != Role.ADMIN) {
            throw new RuntimeException("Access denied");
        }

        List<Borrow> borrows = borrowService.getAllBorrows();

        long totalRequests = borrowService.getAllBorrows().size();
        long totalBorrows = borrows.size();
        double avgDays = borrows.stream()
                .filter(b -> b.getReturnDate() != null)
                .mapToLong(b -> java.time.temporal.ChronoUnit.DAYS.between(b.getApproveDate(), b.getReturnDate()))
                .average()
                .orElse(0);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRequests", totalRequests);
        stats.put("totalBorrows", totalBorrows);
        stats.put("averageBorrowDays", avgDays);

        return ResponseEntity.ok(stats);
    }

    // 🔹 گزارش عملکرد کارمند (مدیر)
    @GetMapping("/employees/{username}/performance")
    public ResponseEntity<Map<String, Object>> getEmployeePerformance(
            @PathVariable String username,
            @RequestHeader("role") Role role
    ) {
        if (role != Role.ADMIN) {
            throw new RuntimeException("Access denied");
        }

        List<Borrow> borrows = borrowService.getAllBorrows();
        List<Borrow> borrowsByEmployee = borrows.stream()
                .filter(b -> b.getEmployeeUsername().equals(username))
                .collect(Collectors.toList());

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBooksRegistered", bookService.getBooks(null, null, null).stream()
                .filter(b -> b.getRegisteredBy().equals(username)).count());
        stats.put("totalBooksBorrowed", borrowsByEmployee.size());
        stats.put("totalBooksReturned", borrowsByEmployee.stream()
                .filter(b -> b.getReturnDate() != null).count());

        return ResponseEntity.ok(stats);
    }

    // 🔹 لیست دانشجویان با بیشترین تاخیر (مدیر)
    @GetMapping("/top-delayed")
    public ResponseEntity<List<Map<String, Object>>> getTopDelayedStudents(
            @RequestHeader("role") Role role
    ) {
        if (role != Role.ADMIN) {
            throw new RuntimeException("Access denied");
        }

        List<Borrow> borrows = borrowService.getAllBorrows().stream()
                .filter(b -> b.getReturnDate() != null)
                .collect(Collectors.toList());

        Map<String, Double> delays = new HashMap<>();

        for (Borrow b : borrows) {
            long days = java.time.temporal.ChronoUnit.DAYS.between(b.getApproveDate(), b.getReturnDate());
            // اگر بیش از 7 روز
            if (days > 7) {
                delays.put(b.getStudentUsername(),
                        delays.getOrDefault(b.getStudentUsername(), 0.0) + days - 7);
            }
        }

        List<Map<String, Object>> result = delays.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("studentUsername", e.getKey());
                    map.put("delayedDays", e.getValue());
                    return map;
                })
                .collect(Collectors.toList());


        return ResponseEntity.ok(result);
    }
}