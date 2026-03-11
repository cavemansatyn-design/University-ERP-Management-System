package edu.univ.erp.domain;

public class EnrolledStudent {
    private int enrollmentId;
    private String studentName;
    private String rollNo;
    private String program;

    public EnrolledStudent(int enrollmentId, String studentName, String rollNo, String program)
    {
        this.enrollmentId = enrollmentId;
        this.studentName = studentName;
        this.rollNo = rollNo;
        this.program = program;
    }

    public int getEnrollmentId() { return enrollmentId; }
    public String getStudentName() { return studentName; }
    public String getRollNo() { return rollNo; }
    public String getProgram() { return program; }

    @Override
    public String toString() {

        return rollNo + "  -  " + studentName;
    }
}