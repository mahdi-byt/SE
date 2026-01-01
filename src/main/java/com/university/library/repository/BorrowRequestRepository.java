package com.university.library.repository;

import com.university.library.model.BorrowRequest;
import com.university.library.repository.wrapper.BorrowRequestsWrapper;
import com.university.library.util.JsonFileUtil;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BorrowRequestRepository {

    private static final String REQUESTS_FILE = "data/BorrowRequests.json";

    public List<BorrowRequest> findAll() {
        return JsonFileUtil
                .readFromFile(REQUESTS_FILE, BorrowRequestsWrapper.class)
                .getRequests();
    }

    public void saveAll(List<BorrowRequest> requests) {
        BorrowRequestsWrapper wrapper = new BorrowRequestsWrapper();
        wrapper.setRequests(requests);
        JsonFileUtil.writeToFile(REQUESTS_FILE, wrapper);
    }
}