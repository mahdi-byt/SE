package com.university.library.controller.borrow;

import com.university.library.dto.ApiResponse;
import com.university.library.model.Borrow;
import com.university.library.model.BorrowRequest;
import com.university.library.model.enums.Role;
import com.university.library.service.BorrowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrow")
public class BorrowController {

    private final BorrowService borrowService;

    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    // 🔹 ثبت درخواست امانت جدید (دانشجو)
    @PostMapping("/request")
    public ResponseEntity<ApiResponse> requestBorrow(
            @RequestHeader("username") String username,
            @RequestHeader("role") Role role,
            @RequestParam String isbn
    ) {
        borrowService.requestBorrow(isbn, username, role);
        return ResponseEntity.ok(new ApiResponse(true, "Borrow request submitted"));
    }

    // 🔹 مشاهده درخواست‌های در انتظار تایید (کارمند)
    @GetMapping("/requests/pending")
    public ResponseEntity<List<BorrowRequest>> getPendingRequests(
            @RequestHeader("role") Role role
    ) {
        List<BorrowRequest> pendingRequests = borrowService.getPendingRequests(role);
        return ResponseEntity.ok(pendingRequests);
    }

    // 🔹 تایید درخواست امانت (کارمند)
    @PostMapping("/requests/{isbn}/approve")
    public ResponseEntity<ApiResponse> approveRequest(
            @PathVariable String isbn,
            @RequestParam String studentUsername,
            @RequestHeader("username") String employeeUsername,
            @RequestHeader("role") Role role
    ) {
        borrowService.approveRequest(isbn, studentUsername, employeeUsername, role);
        return ResponseEntity.ok(new ApiResponse(true, "Borrow request approved"));
    }

    // 🔹 رد درخواست امانت (کارمند)
    @PostMapping("/requests/{isbn}/reject")
    public ResponseEntity<ApiResponse> rejectRequest(
            @PathVariable String isbn,
            @RequestParam String studentUsername,
            @RequestHeader("role") Role role
    ) {
        borrowService.rejectRequest(isbn, studentUsername, role);
        return ResponseEntity.ok(new ApiResponse(true, "Borrow request rejected"));
    }

    // 🔹 ثبت بازگرداندن کتاب (کارمند)
    @PostMapping("/{isbn}/return")
    public ResponseEntity<ApiResponse> returnBook(
            @PathVariable String isbn,
            @RequestHeader("username") String employeeUsername,
            @RequestHeader("role") Role role
    ) {
        borrowService.returnBook(isbn, employeeUsername, role);
        return ResponseEntity.ok(new ApiResponse(true, "Book returned successfully"));
    }
}