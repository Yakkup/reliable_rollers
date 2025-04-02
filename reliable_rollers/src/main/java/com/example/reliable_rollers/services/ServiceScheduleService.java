package com.example.reliable_rollers.services;

import com.example.reliable_rollers.config.JwtUtil;
import com.example.reliable_rollers.entities.*;
import com.example.reliable_rollers.repositories.EmployeeRepository;
import com.example.reliable_rollers.repositories.ServiceLogRepository;
import com.example.reliable_rollers.repositories.ServiceScheduleRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServiceScheduleService {

    @Autowired
    private ServiceScheduleRepository serviceScheduleRepository;

    @Autowired
    private ServiceLogRepository serviceLogRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private JwtUtil jwtUtil;


    public List<ServiceSchedule> getAllSchedules() {
        return serviceScheduleRepository.findAll();
    }

    public Optional<ServiceSchedule> getScheduleById(Long id) {
        return serviceScheduleRepository.findById(id);
    }

    @Transactional
    public void completeService(Long scheduleId, ServiceLog serviceLog) {
        ServiceSchedule schedule = serviceScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Service Schedule not found"));

        String token = extractJwtToken();
        if (token == null) {
            throw new RuntimeException("JWT token not found in request");
        }

        String employeeEmail = jwtUtil.extractEmail(token);

        Employee employee = employeeRepository.findByEmail(employeeEmail)
                .orElseThrow(() -> new RuntimeException("Employee not found with email: " + employeeEmail));

        serviceLog.setEmployee(employee);
        serviceLog.setCustomer(schedule.getCustomer());
        serviceLog.setServiceDate(schedule.getNextPickupDate());

        serviceLog.setStatus(ServiceLog.ServiceStatus.COMPLETED);

        serviceLogRepository.save(serviceLog);
        System.out.println("✅ Service Log saved for Schedule ID: " + scheduleId);

        if (serviceScheduleRepository.existsById(scheduleId)) {
            System.out.println("🔹 Attempting to delete Service Schedule ID: " + scheduleId);
            serviceScheduleRepository.delete(schedule);
            System.out.println("✅ Service Schedule deleted successfully.");
        } else {
            System.out.println("⚠️ Service Schedule not found, deletion skipped.");
        }

        createNextServiceSchedule(schedule.getCustomer());
    }

    private String extractJwtToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return null;

        HttpServletRequest request = attributes.getRequest();
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    public void createNextServiceSchedule(Customer customer) {
        LocalDate nextPickupDate = getNextPickupDate(customer.getGarbagePickupDay());

        ServiceSchedule newSchedule = new ServiceSchedule();
        newSchedule.setCustomer(customer);
        newSchedule.setCustomerName(customer.getFirstName() + " " + customer.getLastName());
        newSchedule.setCustomerAddress(customer.getAddress());
        newSchedule.setGarbagePickupDay(customer.getGarbagePickupDay());
        newSchedule.setNextPickupDate(nextPickupDate);

        serviceScheduleRepository.save(newSchedule);
        System.out.println("✅ New Service Schedule created for customer ID: " + customer.getId() +
                " with next pickup on " + nextPickupDate);
    }

    @Transactional
    public void deleteSchedule(Long scheduleId) {
        ServiceSchedule schedule = serviceScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Service Schedule not found"));

        serviceScheduleRepository.delete(schedule);
    }

    private LocalDate getNextPickupDate(String pickupDay) {
        if (pickupDay == null || pickupDay.trim().isEmpty()) {
            throw new IllegalArgumentException("Pickup day cannot be null or empty");
        }

        try {
            DayOfWeek targetDay = DayOfWeek.valueOf(pickupDay.toUpperCase());
            LocalDate today = LocalDate.now();
            LocalDate nextPickupDate = today.plusWeeks(1);

            while (nextPickupDate.getDayOfWeek() != targetDay) {
                nextPickupDate = nextPickupDate.plusDays(1);
            }

            return nextPickupDate;
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid garbage pickup day: " + pickupDay);
            throw new RuntimeException("Invalid garbage pickup day: " + pickupDay);
        }
    }

    public List<ServiceSchedule> getServicesDueToday() {
        LocalDate today = LocalDate.now();
        return serviceScheduleRepository.findByNextPickupDate(today);
    }

    public List<ServiceSchedule> getServicesDueTomorrow() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        return serviceScheduleRepository.findByNextPickupDate(tomorrow);
    }

    public byte[] generateReport(LocalDate startDate, LocalDate endDate) {
        List<ServiceSchedule> schedules = serviceScheduleRepository
                .findByNextPickupDateBetween(startDate, endDate);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(outputStream);

        writer.println("Customer Name, Address, Garbage Pickup Day, Next Service Date, Report Generated");

        LocalDateTime timestamp = LocalDateTime.now();
        for (ServiceSchedule schedule : schedules) {
            writer.printf("%s, %s, %s, %s, %s%n",
                    schedule.getCustomerName(),
                    schedule.getCustomerAddress(),
                    schedule.getGarbagePickupDay(),
                    schedule.getNextPickupDate(),
                    timestamp);
        }

        writer.flush();
        return outputStream.toByteArray();
    }
}
