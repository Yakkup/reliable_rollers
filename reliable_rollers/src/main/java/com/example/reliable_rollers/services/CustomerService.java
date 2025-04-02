package com.example.reliable_rollers.services;

import com.example.reliable_rollers.entities.Customer;
import com.example.reliable_rollers.entities.ServiceSchedule;
import com.example.reliable_rollers.repositories.CustomerRepository;
import com.example.reliable_rollers.repositories.ServiceScheduleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ServiceScheduleRepository serviceScheduleRepository;

    public List<Customer> getAllCustomers() {
        logger.info("Fetching all customers");
        return customerRepository.findAll();
    }

    public Optional<Customer> getCustomerById(Long id) {
        logger.info("Fetching customer with ID: {}", id);
        return customerRepository.findById(id);
    }

    public Customer createCustomer(Customer customer) {
        customerRepository.findByEmail(customer.getEmail()).ifPresent(existing -> {
            throw new RuntimeException("Email is already in use");
        });

        logger.info("Creating new customer: {}", customer.getEmail());
        Customer savedCustomer = customerRepository.save(customer);

        if (customer.getGarbagePickupDay() != null) {
            LocalDate nextPickupDate = getNextPickupDate(customer.getGarbagePickupDay());

            ServiceSchedule schedule = new ServiceSchedule();
            schedule.setCustomer(savedCustomer);
            schedule.setGarbagePickupDay(customer.getGarbagePickupDay());
            schedule.setNextPickupDate(nextPickupDate);
            schedule.setScheduleDate(LocalDate.now());

            serviceScheduleRepository.save(schedule);
            logger.info("Service schedule created for customer ID: {} with next pickup on {}", savedCustomer.getId(), nextPickupDate);
        }

        return savedCustomer;
    }

    public Customer updateCustomer(Long id, Customer customerDetails) {
        return customerRepository.findById(id)
                .map(customer -> {
                    logger.info("Updating customer with ID: {}", id);
                    customer.setFirstName(customerDetails.getFirstName());
                    customer.setLastName(customerDetails.getLastName());
                    customer.setEmail(customerDetails.getEmail());
                    customer.setPhone(customerDetails.getPhone());
                    customer.setAddress(customerDetails.getAddress());

                    if (customer.getGarbagePickupDay() != null &&
                            !customer.getGarbagePickupDay().equals(customerDetails.getGarbagePickupDay())) {
                        logger.info("Updating service schedule for customer ID: {}", id);
                        customer.setGarbagePickupDay(customerDetails.getGarbagePickupDay());
                        updateServiceSchedule(customer);
                    }

                    return customerRepository.save(customer);
                }).orElseThrow(() -> {
                    logger.error("Customer not found with ID: {}", id);
                    return new RuntimeException("Customer not found");
                });
    }

    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            logger.error("Attempted to delete non-existing customer with ID: {}", id);
            throw new RuntimeException("Customer not found");
        }

        logger.info("Deleting customer with ID: {}", id);
        customerRepository.deleteById(id);

        serviceScheduleRepository.findByCustomerId(id).ifPresent(schedule -> {
            logger.info("Deleting associated service schedule for customer ID: {}", id);
            serviceScheduleRepository.delete(schedule);
        });
    }

    private LocalDate getNextPickupDate(String pickupDay) {
        if (pickupDay == null || pickupDay.trim().isEmpty()) {
            throw new IllegalArgumentException("Pickup day cannot be null or empty");
        }

        try {
            DayOfWeek targetDay = DayOfWeek.valueOf(pickupDay.toUpperCase());
            LocalDate today = LocalDate.now();

            while (today.getDayOfWeek() != targetDay) {
                today = today.plusDays(1);
            }

            return today;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid garbage pickup day: {}", pickupDay);
            throw new RuntimeException("Invalid garbage pickup day: " + pickupDay);
        }
    }

    private void updateServiceSchedule(Customer customer) {
        serviceScheduleRepository.findByCustomerId(customer.getId()).ifPresent(schedule -> {
            schedule.setGarbagePickupDay(customer.getGarbagePickupDay());
            schedule.setNextPickupDate(getNextPickupDate(customer.getGarbagePickupDay()));
            serviceScheduleRepository.save(schedule);
            logger.info("Updated service schedule for customer ID: {}", customer.getId());
        });
    }
}
