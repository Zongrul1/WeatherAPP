package com.example.weather;

public class User {
    private int currentlevel;
    public User(int currentlevel){
        this.currentlevel = currentlevel;
    }

    public void setCurrentlevel(int currentlevel) {
        this.currentlevel = currentlevel;
    }

    public int getCurrentlevel() {
        return currentlevel;
    }
}
