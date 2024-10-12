package com.moutamid.fallsdeliveryadmin.models;

public class UserModel {

    public String id, name, email, password;

    public UserModel() {
    }

    public UserModel(String id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }
}
