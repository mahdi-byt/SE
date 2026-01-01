package com.university.library.controller.book;

import com.university.library.dto.ApiResponse;
import com.university.library.model.Book;
import com.university.library.model.enums.Role;
import com.university.library.service.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // 🔹 دریافت لیست کتاب‌ها با فیلتر (Query Params)
    // مثال: /api/books?title=Java&author=Bloch&year=2021
    @GetMapping
    public ResponseEntity<List<Book>> getBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Integer year
    ) {
        List<Book> books = bookService.getBooks(title, author, year);
        return ResponseEntity.ok(books);
    }

    // 🔹 دریافت جزئیات یک کتاب
    @GetMapping("/{isbn}")
    public ResponseEntity<Book> getBookByIsbn(@PathVariable String isbn) {
        Book book = bookService.getBookByIsbn(isbn);
        return ResponseEntity.ok(book);
    }

    // 🔹 ایجاد کتاب جدید (کارمند)
    @PostMapping
    public ResponseEntity<ApiResponse> addBook(
            @RequestHeader("username") String username,
            @RequestHeader("role") Role role,
            @RequestBody Book book
    ) {
        bookService.addBook(book, username, role);
        return ResponseEntity.ok(
                new ApiResponse(true, "Book added successfully")
        );
    }

    // 🔹 به‌روزرسانی اطلاعات کتاب (کارمند)
    @PutMapping("/{isbn}")
    public ResponseEntity<ApiResponse> updateBook(
            @PathVariable String isbn,
            @RequestHeader("role") Role role,
            @RequestBody Book updatedBook
    ) {
        bookService.updateBook(isbn, updatedBook, role);
        return ResponseEntity.ok(
                new ApiResponse(true, "Book updated successfully")
        );
    }
}