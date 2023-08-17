package com.example.todoAppjava.task;

import com.example.todoAppjava.user.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskServer {
     private final TaskResposity taskResposity;

     public TaskServer(TaskResposity taskResposity) {
         this.taskResposity = taskResposity;
     }

     public List<Task> getTasks() {
         return taskResposity.findAll();
     }

     public void addTask(String content, User userDetails, String status) {
         Task myTask = new Task(content,status, userDetails.getId());
         taskResposity.save(myTask);
     }

     public void updateTask(String Id, String content, String status, User user) {
           Optional<Task> taskFind = taskResposity.findMyTask(Id, user.getId());
           if (taskFind.isPresent()) {
               taskFind.get().updateTask(content,status);
           } else {
                throw new IllegalStateException("task not existed");
           }
     }
}
