import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;

class Student extends User {
    private boolean isActive;

    public Student(String username, String password, boolean isActive) {
        super(username, password);
        this.isActive = isActive;
    }

    private static final String USERS_FILE_PATH = "src/main/resources/users.json";
    public static int countStudents() {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(USERS_FILE_PATH);

        if (!file.exists()) {
            System.out.println("No users found.");
            return 0;
        }

        try {
            ObjectNode root = (ObjectNode) mapper.readTree(file);
            ArrayNode usersArray = (ArrayNode) root.get("users");

            int count = 0;
            for (int i = 0; i < usersArray.size(); i++) {
                if (usersArray.get(i).get("role").asText().equals("student")) {
                    count++;
                }
            }

            System.out.println("Number of students: " + count);
            return count;

        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }
}