package com.example.recyclerviewexamplelive.model;

public class Message {
    private String userName;
    private String title;
    private String description;
    private String hour;

    public Message(String userName, String title, String description, String hour) {
        this.userName = userName;
        this.title = title;
        this.description = description;
        this.hour = hour;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }
}
