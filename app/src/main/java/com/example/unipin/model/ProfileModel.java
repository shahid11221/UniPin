package com.example.unipin.model;

public class ProfileModel {
    String firstName, lastName,PhoneNumber,email,image;

    public ProfileModel() {
    }

    public ProfileModel(String firstName, String lastName, String phoneNumber, String email, String image) {
        this.firstName = firstName;
        this.lastName = lastName;
        PhoneNumber = phoneNumber;
        this.email = email;
        this.image = image;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getImage() {
        return image;
    }
}
