package com.university.library.repository;

import com.university.library.model.Borrow;
import com.university.library.repository.wrapper.BorrowListWrapper;
import com.university.library.util.JsonFileUtil;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BorrowRepository {

    private static final String BORROW_FILE = "data/BorrowList.json";

    public List<Borrow> findAll() {
        return JsonFileUtil
                .readFromFile(BORROW_FILE, BorrowListWrapper.class)
                .getBorrowed();
    }

    public void saveAll(List<Borrow> borrows) {
        BorrowListWrapper wrapper = new BorrowListWrapper();
        wrapper.setBorrowed(borrows);
        JsonFileUtil.writeToFile(BORROW_FILE, wrapper);
    }
}