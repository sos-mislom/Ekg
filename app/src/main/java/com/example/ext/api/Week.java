package com.example.ext.api;

import java.util.List;

public class Week {
    List<Day> ListOfDys;
    public Week(List<Day> listOfDys) {
        ListOfDys = listOfDys;
    }

    public List<Day> getListOfDys() {
        return ListOfDys;
    }

    public void setListOfDys(List<Day> listOfDys) {
        this.ListOfDys = listOfDys;
    }
}
