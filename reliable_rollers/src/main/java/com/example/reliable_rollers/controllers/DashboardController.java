package com.example.reliable_rollers.controllers;

import com.example.reliable_rollers.entities.ServiceSchedule;
import com.example.reliable_rollers.services.ServiceScheduleService;
import com.example.reliable_rollers.services.ServiceLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private ServiceScheduleService serviceScheduleService;

    @Autowired
    private ServiceLogService serviceLogService;

    @GetMapping("/services-due-today")
    public ResponseEntity<List<ServiceSchedule>> getServicesDueToday() {
        return ResponseEntity.ok(serviceScheduleService.getServicesDueToday());
    }

    @GetMapping("/services-due-tomorrow")
    public ResponseEntity<List<ServiceSchedule>> getServicesDueTomorrow() {
        return ResponseEntity.ok(serviceScheduleService.getServicesDueTomorrow());
    }

    @GetMapping("/completed-services")
    public ResponseEntity<Map<String, Long>> getCompletedServicesPerMonth() {
        return ResponseEntity.ok(serviceLogService.getCompletedServicesPerMonth());
    }
}

