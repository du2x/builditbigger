package com.udacity.gradle;

import java.util.ResourceBundle;

public class Joker {

    private int current;
    private String[] jokes;

    public Joker(){
        current = 0;
        jokes = ResourceBundle.getBundle("jokes").getString("jokes").split(";");
    }

    public String TellJoke(){
        String to_return = jokes[current];
        this.current = (current+1) % jokes.length;
        return to_return;
    }
}
