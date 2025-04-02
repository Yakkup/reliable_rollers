package com.example.reliable_rollers.unittests;

import com.example.reliable_rollers.controllers.CustomerController;
import com.example.reliable_rollers.entities.Customer;
import com.example.reliable_rollers.services.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");
        testCustomer.setAddress("123 Main St");
        testCustomer.setGarbagePickupDay("Monday");
    }

    @Test
    void testCreateCustomer() {
        when(customerService.createCustomer(any(Customer.class))).thenReturn(testCustomer);

        ResponseEntity<Customer> response = customerController.createCustomer(testCustomer);

        assertNotNull(response.getBody());
        assertEquals("John", response.getBody().getFirstName());
        assertEquals("Doe", response.getBody().getLastName());
        assertEquals("123 Main St", response.getBody().getAddress());
        assertEquals("Monday", response.getBody().getGarbagePickupDay());

        verify(customerService, times(1)).createCustomer(any(Customer.class));
    }

    @Test
    void testCreateCustomerWithMissingFields() {
        Customer invalidCustomer = new Customer();
        invalidCustomer.setFirstName("");
        invalidCustomer.setLastName("");
        invalidCustomer.setAddress(null);
        invalidCustomer.setGarbagePickupDay("");

        when(customerService.createCustomer(any(Customer.class)))
                .thenThrow(new RuntimeException("Missing required fields"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            customerController.createCustomer(invalidCustomer);
        });

        assertEquals("Missing required fields", exception.getMessage());
        verify(customerService, times(1)).createCustomer(any(Customer.class));
    }

    @Test
    void testUpdateCustomer() {
        testCustomer.setAddress("456 Elm St");

        when(customerService.updateCustomer(eq(1L), any(Customer.class))).thenReturn(testCustomer);

        ResponseEntity<Customer> response = customerController.updateCustomer(1L, testCustomer);

        assertNotNull(response.getBody());
        assertEquals("456 Elm St", response.getBody().getAddress());
        verify(customerService, times(1)).updateCustomer(eq(1L), any(Customer.class));
    }

    @Test
    void testDeleteCustomer() {
        doNothing().when(customerService).deleteCustomer(1L);

        ResponseEntity<Void> response = customerController.deleteCustomer(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(customerService, times(1)).deleteCustomer(1L);
    }
}
