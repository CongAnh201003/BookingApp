package com.example.booking.Model;

public class User {
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String identityNumber;
    private String dateOfBirth;
    private String gender;

    public User() {
        // Required for Firebase
    }

    public User(String username, String fullName, String email, String phoneNumber, String identityNumber, String dateOfBirth, String gender) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.identityNumber = identityNumber;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getIdentityNumber() { return identityNumber; }
    public void setIdentityNumber(String identityNumber) { this.identityNumber = identityNumber; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
}
