package com.university.library.model;

import java.time.LocalDate;

public class Borrow {

    private String isbn;
    private String studentUsername;
    private String employeeUsername;
    private LocalDate approveDate;
    private String returnEmployeeUsername;
    private LocalDate returnDate;

    public Borrow() {
    }

    public Borrow(String isbn, String studentUsername, String employeeUsername,
                  LocalDate approveDate) {
        this.isbn = isbn;
        this.studentUsername = studentUsername;
        this.employeeUsername = employeeUsername;
        this.approveDate = approveDate;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getStudentUsername() {
        return studentUsername;
    }

    public void setStudentUsername(String studentUsername) {
        this.studentUsername = studentUsername;
    }

    public String getEmployeeUsername() {
        return employeeUsername;
    }

    public void setEmployeeUsername(String employeeUsername) {
        this.employeeUsername = employeeUsername;
    }

    public LocalDate getApproveDate() {
        return approveDate;
    }

    public void setApproveDate(LocalDate approveDate) {
        this.approveDate = approveDate;
    }

    public String getReturnEmployeeUsername() {
        return returnEmployeeUsername;
    }

    public void setReturnEmployeeUsername(String returnEmployeeUsername) {
        this.returnEmployeeUsername = returnEmployeeUsername;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
}