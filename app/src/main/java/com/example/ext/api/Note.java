package com.example.ext.api;

public class Note {
    String Mark;
    Double value;
    Day day;

    public Note(Day day, String mark, Double value) {
        this.day = day;
        this.Mark = mark;
        this.value = value;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Day getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day = day;
    }

    public String getMark() {
        return Mark;
    }

    public void setMark(String mark) {
        this.Mark = mark;
    }
}
