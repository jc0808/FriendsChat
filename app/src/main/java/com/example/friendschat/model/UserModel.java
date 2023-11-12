package com.example.friendschat.model;

import com.google.firebase.Timestamp;

public class UserModel {

    private String username;
    private String phoneNumber;

    private Timestamp createdTimestamp;
    private String userId;

    private String fcmToken;

    public UserModel() {
    }

    public UserModel(String username, String phoneNumber, Timestamp createdTimestamp, String userId) {
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.createdTimestamp = createdTimestamp;
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
