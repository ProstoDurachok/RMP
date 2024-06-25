package com.example.rmp;

public class Users {
    private String id;
    private String email;
    private String password;

    public Users() {
        // Пустой конструктор, требуемый для Firebase
    }

    public Users(String id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
