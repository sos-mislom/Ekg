package com.example.ext.api;

public class HomeWorkText {
    String theme;
    String homeTask;

    public HomeWorkText(String theme, String homeTask) {
        this.theme = theme;
        this.homeTask = homeTask;
    }

    public String getHomeTask() {
        return homeTask;
    }

    public void setHomeTask(String homeTask) {
        this.homeTask = homeTask;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }
}
