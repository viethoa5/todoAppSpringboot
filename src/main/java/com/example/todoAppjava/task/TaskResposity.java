package com.example.todoAppjava.task;

import com.example.todoAppjava.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface TaskResposity extends MongoRepository<Task, String> {
     @Query("SELECT t FROM Task t WHERE t._id = :Id")
      Optional<Task> findById(String Id);
    @Query("SELECT u FROM User u WHERE u._id = :Id and u.userId = :userId")
    Optional<Task> findMyTask(String Id, String userId);
}
