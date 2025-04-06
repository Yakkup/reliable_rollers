package com.example.reliable_rollers.controllers;

import com.example.reliable_rollers.entities.Employee;
import com.example.reliable_rollers.entities.UserRole;
import com.example.reliable_rollers.repositories.UserRoleRepository;
import com.example.reliable_rollers.services.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        return employeeService.getEmployeeById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody Employee employee) {
        UserRole userRole = userRoleRepository.findByRoleName(employee.getRole().getRoleName())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        employee.setRole(userRole);

        Employee savedEmployee = employeeService.createEmployee(employee);
        return ResponseEntity.ok(savedEmployee);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, employee));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }


    @PutMapping(value = "/{id}/reset-password", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, String>> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String newPassword = request.get("newPassword");

        if (newPassword == null || newPassword.length() < 6) {
            return ResponseEntity.status(400).body(Collections.singletonMap("message", "Password must be at least 6 characters."));
        }

        employeeService.resetPassword(id, newPassword);

        return ResponseEntity.ok(Collections.singletonMap("message", "Password has been reset successfully. User must change password on next login."));
    }
}
