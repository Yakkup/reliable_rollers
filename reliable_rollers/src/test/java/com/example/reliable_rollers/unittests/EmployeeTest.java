package com.example.reliable_rollers.unittests;

import com.example.reliable_rollers.controllers.EmployeeController;
import com.example.reliable_rollers.entities.Employee;
import com.example.reliable_rollers.entities.UserRole;
import com.example.reliable_rollers.services.EmployeeService;
import com.example.reliable_rollers.repositories.UserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeTest {

    @Mock
    private EmployeeService employeeService;

    @Mock
    private UserRoleRepository userRoleRepository;

    @InjectMocks
    private EmployeeController employeeController;

    private Employee testEmployee;
    private UserRole testRole;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testRole = new UserRole();
        testRole.setId(1L);
        testRole.setRoleName("ROLE_USER");

        testEmployee = new Employee();
        testEmployee.setId(1L);
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setEmail("john.doe@example.com");
        testEmployee.setPassword(new BCryptPasswordEncoder().encode("password123"));
        testEmployee.setRole(testRole);
    }

    @Test
    void testCreateEmployee() {
        when(userRoleRepository.findByRoleName("ROLE_USER")).thenReturn(Optional.of(testRole));
        when(employeeService.createEmployee(any(Employee.class))).thenReturn(testEmployee);

        ResponseEntity<Employee> response = employeeController.createEmployee(testEmployee);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("John", response.getBody().getFirstName());
    }

    @Test
    void testCreateEmployeeWithDuplicateEmail() {
        when(userRoleRepository.findByRoleName("ROLE_USER")).thenReturn(Optional.of(testRole));
        when(employeeService.createEmployee(any(Employee.class)))
                .thenThrow(new RuntimeException("Email already exists"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            employeeController.createEmployee(testEmployee);
        });

        assertEquals("Email already exists", exception.getMessage());
    }
}
