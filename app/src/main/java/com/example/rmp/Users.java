package com.example.rmp;

public class Users {
    private String id;
    private String email;
    private String password;
    public Users() {}
    public Users(String id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}
