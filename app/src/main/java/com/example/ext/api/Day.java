package com.example.ext.api;

import java.time.LocalDate;

public class Day {
    HomeWork homeWork;
    LocalDate date;

    public Day(LocalDate date, HomeWork homeWork) {
        this.date = date;
        this.homeWork = homeWork;
    }


    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public HomeWork getHomeWork() {
        return homeWork;
    }

    public void setHomeWork(HomeWork homeWork) {
        this.homeWork = homeWork;
    }


}
