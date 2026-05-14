package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User newUser) {
        // Check if the email already exists
        if (userService.findByEmail(newUser.getEmail()) != null) {
            return ResponseEntity.badRequest().body(new AuthResponse("Email already in use", null, null, null));
        }

        // Save the new user without hashing the password
        userService.saveUser(newUser);
        return ResponseEntity.ok(new AuthResponse("Registration successful", null, null, null));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginUser) {
        // Hardcoded admin login
        if ("admin@mail.com".equals(loginUser.getEmail()) && "Admin".equals(loginUser.getPassword())) {
            return ResponseEntity.ok(new AuthResponse("Admin login successful", "admin-token", "admin", 0L));
        }

        // Check for regular user login
        User user = userService.findByEmail(loginUser.getEmail());

        if (user != null && user.getPassword().equals(loginUser.getPassword())) {
            return ResponseEntity.ok(new AuthResponse(
                "User login successful", 
                "user-token", 
                user.getName(),  // ✅ Returning username
                user.getId()      // ✅ Returning userId
            ));
        } else {
            return ResponseEntity.badRequest().body(new AuthResponse("Invalid credentials", null, null, null));
        }
    }
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody User loginUser) {
//        User user = userService.findByEmail(loginUser.getEmail());
//
//        if (user != null && passwordEncoder.matches(loginUser.getPassword(), user.getPassword())) {
//            return ResponseEntity.ok(new AuthResponse(
//                "User login successful", 
//                "user-token", 
//                user.getName(),  
//                user.getId()      
//            ));
//        } else {
//            return ResponseEntity.badRequest().body(new AuthResponse("Invalid credentials", null, null, null));
//        }
//    }


    // ✅ Updated Response Class with userId and username
    public static class AuthResponse {
        private String message;
        private String token;
        private String username;
        private Long userId;

        public AuthResponse(String message, String token, String username, Long userId) {
            this.message = message;
            this.token = token;
            this.username = username;
            this.userId = userId;
        }

        public String getMessage() { return message; }
        public String getToken() { return token; }
        public String getUsername() { return username; }
        public Long getUserId() { return userId; }
    }
}
