package com.university.library.model;

public class Book {

    private String title;
    private String author;
    private int year;
    private String isbn;
    private String registeredBy;
    private boolean isAvailable;

    // ✅ سازنده بدون آرگومان (ضروری برای Jackson)
    public Book() {
    }

    // ✅ سازنده کامل
    public Book(String title, String author, int year, String isbn,
                String registeredBy, boolean isAvailable) {
        this.title = title;
        this.author = author;
        this.year = year;
        this.isbn = isbn;
        this.registeredBy = registeredBy;
        this.isAvailable = isAvailable;
    }

    // 🔹 Getter و Setter ها

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getRegisteredBy() {
        return registeredBy;
    }

    public void setRegisteredBy(String registeredBy) {
        this.registeredBy = registeredBy;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}