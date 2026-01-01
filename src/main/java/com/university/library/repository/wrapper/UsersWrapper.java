package com.university.library.repository.wrapper;

import com.university.library.model.User;

import java.util.List;

public class UsersWrapper {

    private List<User> users;

    public UsersWrapper() {
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}