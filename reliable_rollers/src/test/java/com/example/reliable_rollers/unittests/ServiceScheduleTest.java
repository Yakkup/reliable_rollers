package com.example.reliable_rollers.unittests;

import com.example.reliable_rollers.controllers.ServiceScheduleController;
import com.example.reliable_rollers.entities.Customer;
import com.example.reliable_rollers.entities.ServiceLog;
import com.example.reliable_rollers.entities.ServiceSchedule;
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
public class ServiceScheduleTest {

    @Mock
    private ServiceScheduleService serviceScheduleService;

    @InjectMocks
    private ServiceScheduleController serviceScheduleController;

    private Customer testCustomer;
    private ServiceSchedule testSchedule;
    private ServiceLog testServiceLog;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");
        testCustomer.setAddress("123 Main St");
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
    }

    @Test
    void testCreateNextServiceScheduleOnCustomerCreation() {
        doNothing().when(serviceScheduleService).createNextServiceSchedule(testCustomer);

        serviceScheduleService.createNextServiceSchedule(testCustomer);

        verify(serviceScheduleService, times(1)).createNextServiceSchedule(testCustomer);
    }

    @Test
    void testServiceCompletionCreatesNextSchedule() {
        doNothing().when(serviceScheduleService).completeService(eq(1L), any(ServiceLog.class));

        ResponseEntity<Void> response = serviceScheduleController.completeService(1L, testServiceLog);

        assertEquals(200, response.getStatusCodeValue());
        verify(serviceScheduleService, times(1)).completeService(eq(1L), any(ServiceLog.class));
    }

    @Test
    void testGetScheduleById() {
        when(serviceScheduleService.getScheduleById(1L)).thenReturn(Optional.of(testSchedule));

        ResponseEntity<ServiceSchedule> response = serviceScheduleController.getScheduleById(1L);

        assertNotNull(response.getBody());
        assertEquals("John Doe", response.getBody().getCustomerName());
        verify(serviceScheduleService, times(1)).getScheduleById(1L);
    }

    @Test
    void testGetScheduleById_NotFound() {
        when(serviceScheduleService.getScheduleById(2L)).thenReturn(Optional.empty());

        ResponseEntity<ServiceSchedule> response = serviceScheduleController.getScheduleById(2L);

        assertEquals(404, response.getStatusCodeValue());
        verify(serviceScheduleService, times(1)).getScheduleById(2L);
    }
}

