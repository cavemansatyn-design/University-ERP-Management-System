package edu.univ.erp.domain;

public class StudentGrade {
    private String component;
    private Double myScore;
    private Double classAverage;

    public StudentGrade(String component, Double myScore, Double classAverage) {
        this.component = component;
        this.myScore = myScore;
        this.classAverage = classAverage;
    }

    public String getComponent() {
        return component;
    }

    public String getMyScoreDisplay() {
        if(myScore == null){
            return "Pending";
        }
        else{
            return String.valueOf(myScore);
        }
    }

    public String getClassAverageDisplay() {
        if(classAverage == null){
            return  "-";
        }
        else{
            return String.format("%.2f", classAverage);
        }

    }
}