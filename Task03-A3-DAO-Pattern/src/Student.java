public class Student {
    private int studentId;
    private String registerNo;
    private String studentName;
    private String email;
    private String department;

    public Student(int studentId, String registerNo, String studentName,
                   String email, String department) {
        this.studentId = studentId;
        this.registerNo = registerNo;
        this.studentName = studentName;
        this.email = email;
        this.department = department;
    }

    public Student(String registerNo, String studentName,
                   String email, String department) {
        this(0, registerNo, studentName, email, department);
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getRegisterNo() {
        return registerNo;
    }

    public void setRegisterNo(String registerNo) {
        this.registerNo = registerNo;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentId=" + studentId +
                ", registerNo='" + registerNo + '\'' +
                ", studentName='" + studentName + '\'' +
                ", email='" + email + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}
