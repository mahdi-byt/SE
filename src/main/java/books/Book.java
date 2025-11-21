package books;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;

public class Book {
    private String title;
    private String author;
    private int year;
    private String isbn;
    private String registeredBy;
    private boolean isAvailable;

    public Book() {
    }

    public Book(String title, String author, int year, String isbn, String registeredBy, boolean isAvailable) {
        this.title = title;
        this.author = author;
        this.year = year;
        this.isbn = isbn;
        this.registeredBy = registeredBy;
        this.isAvailable = isAvailable;
    }

    public static void setBooksFilePath(String path) {
        BOOKS_FILE_PATH = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    private static String BOOKS_FILE_PATH = "src/main/resources/books.json";
    public static int countBooks() {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(BOOKS_FILE_PATH);

        if (!file.exists()) {
            System.out.println("Books.Book file not found!");
            return 0;
        }

        try {
            ObjectNode root = (ObjectNode) mapper.readTree(file);
            ArrayNode booksArray = (ArrayNode) root.get("books");

            if (booksArray == null) {
                System.out.println("No 'books' array found in JSON!");
                return 0;
            }

            int count = booksArray.size();
            System.out.println("Number of books: " + count);
            return count;

        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void searchBookByTitle(String title) {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(BOOKS_FILE_PATH);

        if (!file.exists()) {
            System.out.println("Books.Book file not found!");
            return;
        }

        try {
            ObjectNode root = (ObjectNode) mapper.readTree(file);
            ArrayNode booksArray = (ArrayNode) root.get("books");

            if (booksArray == null) {
                System.out.println("No 'books' array found in JSON!");
                return;
            }

            boolean found = false;
            String searchTerm = title.toLowerCase();

            System.out.println("🔍 Search results for: \"" + title + "\"");
            System.out.println("--------------------------------------------------");

            for (int i = 0; i < booksArray.size(); i++) {
                ObjectNode book = (ObjectNode) booksArray.get(i);
                String bookTitle = book.get("title").asText().toLowerCase();

                // بررسی وجود زیررشته در عنوان
                if (bookTitle.contains(searchTerm)) {
                    System.out.println("📘 Title  : " + book.get("title").asText());
                    System.out.println("    Author : " + book.get("author").asText());
                    System.out.println("    Year   : " + book.get("year").asInt());
                    System.out.println("    ISBN   : " + book.get("isbn").asText());

                    boolean isAvailable = book.has("isAvailable") && book.get("isAvailable").asBoolean();
                    String status = isAvailable ? "✅ Available" : "❌ Not Available";
                    System.out.println("    Status : " + status);

                    System.out.println("--------------------------------------------------");
                    found = true;
                }
            }

            if (!found) {
                System.out.println("No books found containing \"" + title + "\" in title.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void searchBookByYear(int year) {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(BOOKS_FILE_PATH);

        if (!file.exists()) {
            System.out.println("Books.Book file not found!");
            return;
        }

        try {
            ObjectNode root = (ObjectNode) mapper.readTree(file);
            ArrayNode booksArray = (ArrayNode) root.get("books");

            if (booksArray == null) {
                System.out.println("No 'books' array found in JSON!");
                return;
            }

            boolean found = false;

            System.out.println("🔍 Search results for year: " + year);
            System.out.println("--------------------------------------------------");

            for (int i = 0; i < booksArray.size(); i++) {
                ObjectNode book = (ObjectNode) booksArray.get(i);
                int bookYear = book.get("year").asInt();

                if (bookYear == year) {
                    System.out.println("📘 Title  : " + book.get("title").asText());
                    System.out.println("    Author : " + book.get("author").asText());
                    System.out.println("    Year   : " + bookYear);
                    System.out.println("    ISBN   : " + book.get("isbn").asText());

                    boolean isAvailable = book.has("isAvailable") && book.get("isAvailable").asBoolean();
                    String status = isAvailable ? "✅ Available" : "❌ Not Available";
                    System.out.println("    Status : " + status);

                    System.out.println("--------------------------------------------------");
                    found = true;
                }
            }

            if (!found) {
                System.out.println("No books found published in year " + year + ".");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void searchBookByAuthor(String author) {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(BOOKS_FILE_PATH);

        if (!file.exists()) {
            System.out.println("Books.Book file not found!");
            return;
        }

        try {
            ObjectNode root = (ObjectNode) mapper.readTree(file);
            ArrayNode booksArray = (ArrayNode) root.get("books");

            if (booksArray == null) {
                System.out.println("No 'books' array found in JSON!");
                return;
            }

            boolean found = false;
            String searchTerm = author.toLowerCase();

            System.out.println("🔍 Search results for author: \"" + author + "\"");
            System.out.println("--------------------------------------------------");

            for (int i = 0; i < booksArray.size(); i++) {
                ObjectNode book = (ObjectNode) booksArray.get(i);
                String bookAuthor = book.get("author").asText().toLowerCase();

                if (bookAuthor.contains(searchTerm)) {
                    System.out.println("📘 Title  : " + book.get("title").asText());
                    System.out.println("    Author : " + book.get("author").asText());
                    System.out.println("    Year   : " + book.get("year").asInt());
                    System.out.println("    ISBN   : " + book.get("isbn").asText());

                    boolean isAvailable = book.has("isAvailable") && book.get("isAvailable").asBoolean();
                    String status = isAvailable ? "✅ Available" : "❌ Not Available";
                    System.out.println("    Status : " + status);

                    System.out.println("--------------------------------------------------");
                    found = true;
                }
            }

            if (!found) {
                System.out.println("No books found for author \"" + author + "\".");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
