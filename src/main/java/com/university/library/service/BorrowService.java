package com.university.library.service;

import com.university.library.model.BorrowRequest;
import com.university.library.model.enums.BorrowStatus;
import com.university.library.model.Book;
import com.university.library.model.enums.Role;
import com.university.library.repository.BookRepository;
import com.university.library.repository.BorrowRepository;
import com.university.library.repository.BorrowRequestRepository;
import org.springframework.stereotype.Service;
import com.university.library.model.Borrow;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;

@Service
public class BorrowService {

    private final BorrowRequestRepository borrowRequestRepository;
    private final BorrowRepository borrowRepository;
    private final BookRepository bookRepository;

    public BorrowService(BorrowRequestRepository borrowRequestRepository,
                         BorrowRepository borrowRepository,
                         BookRepository bookRepository) {
        this.borrowRequestRepository = borrowRequestRepository;
        this.borrowRepository = borrowRepository;
        this.bookRepository = bookRepository;
    }

    public void requestBorrow(String isbn, String studentUsername, Role role) {

        if (role != Role.STUDENT) {
            throw new RuntimeException("Only students can request borrowing");
        }

        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (!book.isAvailable()) {
            throw new RuntimeException("Book is not available");
        }

        List<BorrowRequest> requests = borrowRequestRepository.findAll();

        boolean alreadyRequested = requests.stream()
                .anyMatch(r -> r.getIsbn().equals(isbn)
                        && r.getUsername().equals(studentUsername)
                        && r.getStatus() == BorrowStatus.PENDING);

        if (alreadyRequested) {
            throw new RuntimeException("Borrow request already exists");
        }

        BorrowRequest request = new BorrowRequest(
                isbn,
                studentUsername,
                LocalDate.now(),
                BorrowStatus.PENDING
        );

        requests.add(request);
        borrowRequestRepository.saveAll(requests);
    }

    public List<BorrowRequest> getPendingRequests(Role role) {

        if (role != Role.EMPLOYEE) {
            throw new RuntimeException("Access denied");
        }

        return borrowRequestRepository.findAll().stream()
                .filter(r -> r.getStatus() == BorrowStatus.PENDING)
                .collect(Collectors.toList());
    }

    public void approveRequest(String isbn, String studentUsername,
                               String employeeUsername, Role role) {

        if (role != Role.EMPLOYEE) {
            throw new RuntimeException("Access denied");
        }

        List<BorrowRequest> requests = borrowRequestRepository.findAll();

        BorrowRequest request = requests.stream()
                .filter(r -> r.getIsbn().equals(isbn)
                        && r.getUsername().equals(studentUsername))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Request not found"));

        requests.remove(request);
        borrowRequestRepository.saveAll(requests);

        Borrow borrow = new Borrow(
                isbn,
                studentUsername,
                employeeUsername,
                LocalDate.now()
        );

        List<Borrow> borrows = borrowRepository.findAll();
        borrows.add(borrow);
        borrowRepository.saveAll(borrows);

        // تغییر وضعیت کتاب
        var books = bookRepository.findAll();
        books.stream()
                .filter(b -> b.getIsbn().equals(isbn))
                .findFirst()
                .ifPresent(b -> b.setAvailable(false));

        bookRepository.saveAll(books);
    }

    public void rejectRequest(String isbn, String studentUsername, Role role) {

        if (role != Role.EMPLOYEE) {
            throw new RuntimeException("Access denied");
        }

        List<BorrowRequest> requests = borrowRequestRepository.findAll();

        BorrowRequest request = requests.stream()
                .filter(r -> r.getIsbn().equals(isbn)
                        && r.getUsername().equals(studentUsername))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus(BorrowStatus.REJECTED);
        borrowRequestRepository.saveAll(requests);
    }

    public void returnBook(String isbn, String employeeUsername, Role role) {

        if (role != Role.EMPLOYEE) {
            throw new RuntimeException("Access denied");
        }

        List<Borrow> borrows = borrowRepository.findAll();

        Borrow borrow = borrows.stream()
                .filter(b -> b.getIsbn().equals(isbn) && b.getReturnDate() == null)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Active borrow not found"));

        borrow.setReturnDate(LocalDate.now());
        borrow.setReturnEmployeeUsername(employeeUsername);

        borrowRepository.saveAll(borrows);

        var books = bookRepository.findAll();
        books.stream()
                .filter(b -> b.getIsbn().equals(isbn))
                .findFirst()
                .ifPresent(b -> b.setAvailable(true));

        bookRepository.saveAll(books);
    }


    public List<Borrow> getBorrowHistoryByStudent(String studentUsername) {
        return borrowRepository.findAll().stream()
                .filter(b -> b.getStudentUsername().equals(studentUsername))
                .collect(Collectors.toList());
    }

    public List<Borrow> getAllBorrows() {
        return borrowRepository.findAll();
    }

    public List<BorrowRequest> getAllRequests() {
        return borrowRequestRepository.findAll();
    }

}