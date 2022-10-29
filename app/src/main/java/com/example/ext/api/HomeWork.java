package com.example.ext.api;

public class HomeWork {
    String SubjectName;
    String TeacherName;
    HomeWorkText homeWorkText;
    Note Mark;

    public HomeWork(String subjectName, String teacherName, HomeWorkText homeWorkText, Note mark) {
        this.SubjectName = subjectName;
        this.TeacherName = teacherName;
        this.homeWorkText = homeWorkText;
        this.Mark = mark;
    }

    public String getSubjectName() {
        return SubjectName;
    }

    public void setSubjectName(String subjectName) {
        this.SubjectName = subjectName;
    }

    public String getTeacherName() {
        return TeacherName;
    }

    public void setTeacherName(String teacherName) {
        this.TeacherName = teacherName;
    }

    public HomeWorkText getHomeWorkText() {
        return homeWorkText;
    }

    public void setHomeWorkText(HomeWorkText homeWorkText) {
        this.homeWorkText = homeWorkText;
    }

    public Note getMark() {
        return Mark;
    }

    public void setMark(Note mark) {
        this.Mark = mark;
    }
}
