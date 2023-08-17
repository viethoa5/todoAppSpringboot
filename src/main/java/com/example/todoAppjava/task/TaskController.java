package com.example.todoAppjava.task;

import com.example.todoAppjava.user.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/tasks")
public class TaskController {
    private final TaskServer taskServer;

    @Autowired
    public TaskController(TaskServer taskServer) {
        this.taskServer = taskServer;
    }

    @GetMapping(value = "")
    public List<Task> getTask(@RequestParam Optional<String> content) {
        return taskServer.getTasks();
    }

    private User getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return user;
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void addTask(@ModelAttribute("content") String content, @ModelAttribute("status") String status) {
        User userDetails = getUserInfo();
        System.out.println("Hello" + content);
        System.out.println("Hello" + status);
        if (userDetails != null) {
            taskServer.addTask(content,userDetails, status);
        }
    }

    @PutMapping(value = "/{Id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateTask(@PathVariable String Id, @ModelAttribute("content") String content, @ModelAttribute("status") String status) {
        User userDetails = getUserInfo();
        if (userDetails != null) {
            taskServer.updateTask(Id, content, status, userDetails);
        }
    }
}
