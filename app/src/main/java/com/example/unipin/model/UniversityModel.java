package com.example.unipin.model;

public class UniversityModel {
    String universityImage,universityName;


    public UniversityModel() {

    }

    public UniversityModel(String universityImage, String universityName) {
        this.universityImage = universityImage;
        this.universityName = universityName;
    }

    public String getUniversityImage() {
        return universityImage;
    }

    public String getUniversityName() {
        return universityName;
    }
}


