package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")  // ✅ Allow frontend access
public class UserController {

    @Autowired
    private UserService userService;

    // ✅ Get all users
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // ✅ Get a user by ID
//    @GetMapping("/{id}")
//    public ResponseEntity<String> getUserById(@PathVariable Long id)
// {
//        Optional<User> user = userService.getUserById(id);
//        return user.map(ResponseEntity::ok)
//                   .orElseGet(() -> ResponseEntity.status(404).body("User not found"));
//    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);

        if (user.isPresent()) {
            return ResponseEntity.ok(user.get()); // ✅ Return 200 OK with User object
        } else {
            return ResponseEntity.status(404).body("User not found"); // ✅ Return 404 with error message
        }
    }
    




    // ✅ Delete a user
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            userService.deleteUser(id);
            return ResponseEntity.ok("User deleted successfully");
        } else {
            return ResponseEntity.status(404).body("User not found");
        }
    }
}
