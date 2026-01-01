package com.university.library.repository.wrapper;

import com.university.library.model.Borrow;
import java.util.List;

public class BorrowListWrapper {

    private List<Borrow> borrowed;

    public BorrowListWrapper() {
    }

    public List<Borrow> getBorrowed() {
        return borrowed;
    }

    public void setBorrowed(List<Borrow> borrowed) {
        this.borrowed = borrowed;
    }
}