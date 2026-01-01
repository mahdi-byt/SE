package com.university.library.repository.wrapper;

import com.university.library.model.Book;

import java.util.List;

public class BooksWrapper {

    private List<Book> books;

    public BooksWrapper() {
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}