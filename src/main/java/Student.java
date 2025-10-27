class Student extends User {
    private int studentId;

    public Student(String username, String password) {
        super(username, password);
        System.out.println("student");
    }
}