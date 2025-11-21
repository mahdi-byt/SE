package users;
import books.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Guest extends User {
    public Guest(String username, String password) {
        super(username, password);
    }
    private static String USERS_FILE_PATH = "src/main/resources/users.json";
    private static String BORROW_FILE_PATH = "src/main/resources/borrow_records.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void setUsersFilePath(String path) {
        USERS_FILE_PATH = path;
    }

    public static void setBorrowFilePath(String path) {
        BORROW_FILE_PATH = path;
    }


    @Override
    public void userMenu() {
        Scanner scanner = new Scanner(System.in);
        int choice = -1;
        boolean exit = false;

        while (!exit) {
            System.out.println("\n====== 👤 Users.Guest Menu ======");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Search Books.Book by Title");
            System.out.println("4. Count Students");
            System.out.println("5. Count Books");
            System.out.println("6. Get Total Borrowed Books");
            System.out.println("7. Show Last Three Borrowed Books");
            System.out.println("0. Exit");
            System.out.print("👉 Enter your choice: ");

            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input! Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.print("Enter username: ");
                    String newUsername = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String newPassword = scanner.nextLine();
                    register(newUsername, newPassword);
                    break;

                case 2:
                    System.out.print("Enter username: ");
                    String username = scanner.nextLine();
                    System.out.print("Enter password: ");
                    String password = scanner.nextLine();
                    User user = User.login(username, password);

                    if (user != null) {
                        System.out.println("✅ Login successful. Welcome " + user.getUsername() + "!");
                        exit = true;
                        openUserMenu(user);
                    } else {
                        System.out.println("❌ Invalid username or password.");
                    }
                    break;

                case 3:
                    System.out.print("Enter book title: ");
                    String title = scanner.nextLine();
                    Book.searchBookByTitle(title);
                    break;

                case 4:
                    int studentCount = Student.countStudents();
                    System.out.println("👩‍🎓 Total Students: " + studentCount);
                    break;

                case 5:
                    int bookCount = Book.countBooks();
                    System.out.println("📚 Total Books: " + bookCount);
                    break;

                case 6:
                    int totalBorrowed = BorrowRecord.getTotalBorrowedBooks();
                    System.out.println("📖 Total Borrowed Books: " + totalBorrowed);
                    break;

                case 7:
                    BorrowRecord.showLastThreeBorrowedBooks();
                    break;

                case 0:
                    System.out.println("👋 Exiting Users.Guest Menu...");
                    exit = true;
                    break;

                default:
                    System.out.println("❌ Invalid choice. Try again.");
            }
        }

        scanner.close();
    }

    private void openUserMenu(User user) {
        if (user instanceof Student student) {
            student.userMenu();
        } else if (user instanceof Employee employee) {
            employee.userMenu();
        } else if (user instanceof Admin admin) {
            admin.userMenu();
        } else {
            System.out.println("⚠️ Unknown user role. Returning to guest menu.");
            userMenu();
        }
    }
    public boolean register(String username, String password) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            File file = new File(USERS_FILE_PATH);
            ObjectNode root;
            ArrayNode usersArray;

            if (file.exists()) {
                root = (ObjectNode) mapper.readTree(file);
                usersArray = (ArrayNode) root.get("users");
                if (usersArray == null) {
                    usersArray = mapper.createArrayNode();
                    root.set("users", usersArray);
                }
            } else {
                root = mapper.createObjectNode();
                usersArray = mapper.createArrayNode();
                root.set("users", usersArray);
            }

            for (int i = 0; i < usersArray.size(); i++) {
                if (usersArray.get(i).get("username").asText().equals(username)) {
                    System.out.println("Username already exists!");
                    return false;
                }
            }

            ObjectNode newUser = mapper.createObjectNode();
            newUser.put("username", username);
            newUser.put("password", password);
            newUser.put("role", "student");

            usersArray.add(newUser);

            mapper.writerWithDefaultPrettyPrinter().writeValue(file, root);

            System.out.println("User registered successfully!");
            return true;

        } catch (IOException e) {
            System.out.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

    public int getBorrowedBooksCount() {
        try {
            File borrowFile = new File(BORROW_FILE_PATH);
            if (!borrowFile.exists()) return 0;

            JsonNode root = mapper.readTree(borrowFile);
            ArrayNode borrowArray = (ArrayNode) root.get("borrowRecords");
            if (borrowArray == null) return 0;

            int count = 0;
            for (JsonNode record : borrowArray) {
                if (record.has("returnDate") && record.get("returnDate").isNull()) {
                    count++;
                }
            }
            return count;

        } catch (IOException e) {
            System.out.println("Error counting borrowed books: " + e.getMessage());
            return 0;
        }
    }




}