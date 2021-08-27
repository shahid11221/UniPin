package com.example.unipin.model;

public class NotificationModel {
    String image, date,text;


    public NotificationModel() {

    }

    public NotificationModel(String image, String date,String text) {
        this.image = image;
        this.date = date;
        this.text = text;
    }

    public String getImage() {
        return image;
    }
    public String getText() {
        return text;
    }
    public String getDate() {
        return date;
    }
}


