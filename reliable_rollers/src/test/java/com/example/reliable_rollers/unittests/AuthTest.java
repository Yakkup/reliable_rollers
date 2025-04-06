package com.example.reliable_rollers.unittests;

import com.example.reliable_rollers.controllers.AuthController;
import com.example.reliable_rollers.dto.LoginRequest;
import com.example.reliable_rollers.entities.Employee;
import com.example.reliable_rollers.config.JwtUtil;
import com.example.reliable_rollers.repositories.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class AuthTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoginWithValidCredentials() {
        LoginRequest request = new LoginRequest("admin@example.com", "password123");

        Employee employee = new Employee();
        employee.setEmail("admin@example.com");
        employee.setPassword(new BCryptPasswordEncoder().encode("password123"));
        employee.setForcePasswordChange(false);

        when(employeeRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(employee));
        when(passwordEncoder.matches(request.getPassword(), employee.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(employee)).thenReturn("mocked_jwt_token");

        ResponseEntity<?> response = authController.login(request);

        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("token"));
        assertEquals(false, responseBody.get("forcePasswordChange"));
    }

    @Test
    public void testLoginWithInvalidCredentials() {
        LoginRequest request = new LoginRequest("wrong@example.com", "wrongpassword");

        when(employeeRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.login(request);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Invalid email or password.", response.getBody());
    }
}
