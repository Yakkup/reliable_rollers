package com.example.reliable_rollers.controllers;

import com.example.reliable_rollers.dto.LoginRequest;
import com.example.reliable_rollers.entities.Employee;
import com.example.reliable_rollers.entities.UserRole;
import com.example.reliable_rollers.repositories.EmployeeRepository;
import com.example.reliable_rollers.repositories.UserRoleRepository;
import com.example.reliable_rollers.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Employee request) {

        UserRole role = userRoleRepository.findByRoleName(request.getRole().getRoleName())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        Employee employee = new Employee();
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPassword(passwordEncoder.encode(request.getPassword()));
        employee.setRole(role);

        employeeRepository.save(employee);
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<Employee> employeeOpt = employeeRepository.findByEmail(request.getEmail());

        if (employeeOpt.isEmpty() || !passwordEncoder.matches(request.getPassword(), employeeOpt.get().getPassword())) {
            return ResponseEntity.status(401).body("Invalid email or password.");
        }

        Employee employee = employeeOpt.get();
        String token = jwtUtil.generateToken(employee);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("forcePasswordChange", employee.isForcePasswordChange());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request, @RequestHeader("Authorization") String token) {
        String email = jwtUtil.extractEmail(token.substring(7));
        Optional<Employee> employeeOpt = employeeRepository.findByEmail(email);

        if (employeeOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found.");
        }

        Employee employee = employeeOpt.get();
        String newPassword = request.get("newPassword");

        if (newPassword == null || newPassword.length() < 6) {
            return ResponseEntity.status(400).body("Password must be at least 6 characters.");
        }

        employee.setPassword(passwordEncoder.encode(newPassword));
        employee.setForcePasswordChange(false);
        employeeRepository.save(employee);

        return ResponseEntity.ok(Collections.singletonMap("message", "Password updated successfully."));
    }
}
