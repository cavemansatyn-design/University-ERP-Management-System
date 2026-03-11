package edu.univ.erp.domain;


public class CourseCatalog {

    private int sectionId;
    private String courseCode;
    private String courseTitle;
    private String instructorName;
    private String dayTime;
    private String room;
    private int credits;
    private int capacity;


    public CourseCatalog(int sectionId, String courseCode, String courseTitle, String instructorName, String dayTime, String room, int credits, int capacity) {
        this.sectionId = sectionId;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;


        this.instructorName = instructorName;
        this.dayTime = dayTime;
        this.room = room;
        this.credits = credits;
        this.capacity = capacity;
    }


    public int getSectionId() {
        return sectionId;
    }
    public String getCourseCode() {
        return courseCode;
    }
    public String getCourseTitle() {
        return courseTitle;
    }
    public String getInstructorName() {
        return instructorName;
    }
    public String getDayTime() {
        return dayTime;
    }
    public String getRoom() {
        return room;
    }
    public int getCredits() {
        return credits;
    }
    public int getCapacity() {
        return capacity;
    }
}
