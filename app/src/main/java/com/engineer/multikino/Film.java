package com.engineer.multikino;

import java.io.Serializable;
import java.util.ArrayList;

public class Film implements Serializable {
    private String title;
    private ArrayList<String> seances;

    public Film(String title, ArrayList<String> seances) {
        this.title = title;
        this.seances = seances;
    }

    public String getTitle() { return title; }
    public ArrayList<String> getSeances() { return seances; }
}