package com.university.library.model;

import com.university.library.model.enums.BorrowStatus;

import java.time.LocalDate;

public class BorrowRequest {

    private String isbn;
    private String username;
    private LocalDate requestDate;
    private BorrowStatus status;

    // ✅ سازنده بدون آرگومان (ضروری برای Jackson)
    public BorrowRequest() {
    }

    // ✅ سازنده کامل
    public BorrowRequest(String isbn, String username, LocalDate requestDate, BorrowStatus status) {
        this.isbn = isbn;
        this.username = username;
        this.requestDate = requestDate;
        this.status = status;
    }

    // 🔹 Getter و Setter ها

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }

    public BorrowStatus getStatus() {
        return status;
    }

    public void setStatus(BorrowStatus status) {
        this.status = status;
    }
}