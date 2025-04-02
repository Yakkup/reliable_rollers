package com.example.reliable_rollers.unittests;

import com.example.reliable_rollers.controllers.ServiceScheduleController;
import com.example.reliable_rollers.entities.*;
import com.example.reliable_rollers.services.ServiceScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceLogTest {

    @Mock
    private ServiceScheduleService serviceScheduleService;

    @InjectMocks
    private ServiceScheduleController serviceScheduleController;

    private Customer testCustomer;
    private ServiceSchedule testSchedule;
    private ServiceLog testServiceLog;
    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testEmployee = new Employee();
        testEmployee.setId(1L);
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setEmail("employee@example.com");

        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setFirstName("Alice");
        testCustomer.setLastName("Smith");
        testCustomer.setAddress("456 Oak Street");
        testCustomer.setGarbagePickupDay("Monday");

        testSchedule = new ServiceSchedule();
        testSchedule.setId(1L);
        testSchedule.setCustomer(testCustomer);
        testSchedule.setCustomerName(testCustomer.getFirstName() + " " + testCustomer.getLastName());
        testSchedule.setCustomerAddress(testCustomer.getAddress());
        testSchedule.setGarbagePickupDay(testCustomer.getGarbagePickupDay());
        testSchedule.setNextPickupDate(LocalDate.now().plusWeeks(1));

        testServiceLog = new ServiceLog();
        testServiceLog.setId(1L);
        testServiceLog.setServiceDate(LocalDate.now());
        testServiceLog.setStatus(ServiceLog.ServiceStatus.OPEN);
        testServiceLog.setEmployee(testEmployee);
        testServiceLog.setCustomer(testCustomer);
    }

    @Test
    void testCompleteServiceMarksLogAsCompleted() {
        doNothing().when(serviceScheduleService).completeService(eq(1L), any(ServiceLog.class));

        ResponseEntity<Void> response = serviceScheduleController.completeService(1L, testServiceLog);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(ServiceLog.ServiceStatus.OPEN, testServiceLog.getStatus());

        testServiceLog.setStatus(ServiceLog.ServiceStatus.COMPLETED);
        assertEquals(ServiceLog.ServiceStatus.COMPLETED, testServiceLog.getStatus());

        verify(serviceScheduleService, times(1)).completeService(eq(1L), any(ServiceLog.class));
    }

    @Test
    void testServiceScheduleDeletedAfterCompletion() {
        doNothing().when(serviceScheduleService).completeService(eq(1L), any(ServiceLog.class));
        doNothing().when(serviceScheduleService).deleteSchedule(1L);

        serviceScheduleService.completeService(1L, testServiceLog);
        serviceScheduleService.deleteSchedule(1L);

        verify(serviceScheduleService, times(1)).deleteSchedule(1L);
    }

    @Test
    void testNextServiceScheduleCreatedAfterCompletion() {
        doNothing().when(serviceScheduleService).createNextServiceSchedule(testCustomer);

        serviceScheduleService.createNextServiceSchedule(testCustomer);

        verify(serviceScheduleService, times(1)).createNextServiceSchedule(testCustomer);
    }
}

