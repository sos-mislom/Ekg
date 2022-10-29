package com.example.ext.api;

import java.util.List;

public class SubjectNotes {
    String SubjectName;
    List<Note> ListOfNotes;
    Double Average;

    public SubjectNotes(String subjectName, List<Note> listOfNotes, Double average) {
        this.SubjectName = subjectName;
        this.ListOfNotes = listOfNotes;
        this.Average = average;
    }

    public String getSubjectName() {
        return SubjectName;
    }

    public void setSubjectName(String subjectName) {
        this.SubjectName = subjectName;
    }

    public List<Note> getListOfNotes() {
        return ListOfNotes;
    }

    public void setListOfNotes(List<Note> listOfNotes) {
        this.ListOfNotes = listOfNotes;
    }

    public Double getAverage() {
        return Average;
    }

    public void setAverage(Double average) {
        this.Average = average;
    }



}
