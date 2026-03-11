package edu.univ.erp.domain;

public class StudentScore {
    private String rollNo;
    private String studentName;
    private Double score;

    public StudentScore(String rollNo, String studentName, Double score) {
        this.rollNo = rollNo;
        this.studentName = studentName;
        this.score = score;
    }

    public String getRollNo() {
        return rollNo;
    }
    public String getStudentName() {
        return studentName;
    }

    public String getScoreDisplay() {

        if(score == null){
            return "Not Graded";
        }
        else{
            return String.valueOf(score);
        }
    }

}