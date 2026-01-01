package com.university.library.repository.wrapper;

import com.university.library.model.BorrowRequest;

import java.util.List;

public class BorrowRequestsWrapper {

    private List<BorrowRequest> requests;

    public BorrowRequestsWrapper() {
    }

    public List<BorrowRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<BorrowRequest> requests) {
        this.requests = requests;
    }
}