package main;

import users.Guest;

public class Main {
    public static void main(String[] args) {
        Guest user = new Guest("guest", "guest");
        user.userMenu();

    }
}
