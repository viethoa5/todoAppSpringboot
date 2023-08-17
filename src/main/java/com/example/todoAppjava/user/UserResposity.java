package com.example.todoAppjava.user;

import com.example.todoAppjava.task.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface UserResposity extends MongoRepository<User,String> {
    @Query("SELECT u FROM User u WHERE u._id = :Id")
    Optional<User> findById(String Id);
    Optional<User> findByEmail(String email);
}
