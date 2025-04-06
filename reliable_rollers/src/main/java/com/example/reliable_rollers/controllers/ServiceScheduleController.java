package com.example.reliable_rollers.controllers;

import com.example.reliable_rollers.entities.ServiceLog;
import com.example.reliable_rollers.entities.ServiceSchedule;
import com.example.reliable_rollers.services.ServiceScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/service-schedules")
public class ServiceScheduleController {

    @Autowired
    private ServiceScheduleService serviceScheduleService;

    @GetMapping
    public ResponseEntity<List<ServiceSchedule>> getAllSchedules() {
        List<ServiceSchedule> schedules = serviceScheduleService.getAllSchedules();
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceSchedule> getScheduleById(@PathVariable Long id) {
        Optional<ServiceSchedule> schedule = serviceScheduleService.getScheduleById(id);
        return schedule.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{scheduleId}/complete")
    public ResponseEntity<Void> completeService(
            @PathVariable Long scheduleId,
            @RequestBody ServiceLog serviceLog) {

        serviceScheduleService.completeService(scheduleId, serviceLog);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        serviceScheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportServiceSchedules(
            @RequestParam String startDate,
            @RequestParam(required = false) String endDate) {

        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate end = (endDate != null) ? LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE) : start;

        byte[] reportData = serviceScheduleService.generateReport(start, end);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=service_schedules_report.csv");
        headers.add("Content-Type", "text/csv");

        return new ResponseEntity<>(reportData, headers, HttpStatus.OK);
    }
}
