package com.example.todoAppjava.task;

import lombok.Getter;
import org.springframework.data.annotation.Id;

public class Task {
    @Getter
    private String content;
    @Id
    private String Id;
    private String status;
    private String userId;

    public Task(String content, String status, String userId) {
        this.content = content;
        this.status  = status;
        this.userId  = userId;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void updateTask(String content, String status) {
        setContent(content);
        setStatus(status);
    }

    @Override
    public String toString() {
        return "Task{" +
                "content='" + content + '\'' +
                ", Id='" + Id + '\'' +
                ", status='" + status + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
