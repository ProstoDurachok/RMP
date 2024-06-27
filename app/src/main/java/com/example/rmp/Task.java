package com.example.rmp;

public class Task {
    public String taskId;
    public String taskName;
    public boolean isChecked;

    public Task() {
        // Пустой конструктор, необходим для Firebase
    }

    public Task(String taskId, String taskName, boolean isChecked) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.isChecked = isChecked;
    }
}
