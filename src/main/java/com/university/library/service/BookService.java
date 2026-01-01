package com.university.library.service;

import com.university.library.model.Book;
import com.university.library.model.enums.Role;
import com.university.library.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // 🔹 دریافت همه کتاب‌ها با فیلتر
    public List<Book> getBooks(String title, String author, Integer year) {

        return bookRepository.findAll().stream()
                .filter(b -> title == null || b.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(b -> author == null || b.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .filter(b -> year == null || b.getYear() == year)
                .collect(Collectors.toList());
    }

    // 🔹 دریافت کتاب بر اساس ISBN
    public Book getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new RuntimeException("Book not found"));
    }

    // 🔹 افزودن کتاب جدید (فقط کارمند)
    public void addBook(Book book, String currentUsername, Role role) {

        if (role != Role.EMPLOYEE) {
            throw new RuntimeException("Access denied");
        }

        book.setRegisteredBy(currentUsername);
        book.setAvailable(true);

        List<Book> books = bookRepository.findAll();
        books.add(book);

        bookRepository.saveAll(books);
    }

    // 🔹 ویرایش کتاب
    public void updateBook(String isbn, Book updatedBook, Role role) {

        if (role != Role.EMPLOYEE) {
            throw new RuntimeException("Access denied");
        }

        List<Book> books = bookRepository.findAll();

        Book book = books.stream()
                .filter(b -> b.getIsbn().equals(isbn))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Book not found"));

        book.setTitle(updatedBook.getTitle());
        book.setAuthor(updatedBook.getAuthor());
        book.setYear(updatedBook.getYear());
        book.setAvailable(updatedBook.isAvailable());

        bookRepository.saveAll(books);
    }
}