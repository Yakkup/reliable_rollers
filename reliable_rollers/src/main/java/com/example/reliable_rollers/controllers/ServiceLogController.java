package com.example.reliable_rollers.controllers;

import com.example.reliable_rollers.entities.ServiceLog;
import com.example.reliable_rollers.services.ServiceLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/service-logs")
public class ServiceLogController {

    @Autowired
    private ServiceLogService serviceLogService;

    @GetMapping
    public List<ServiceLog> getAllServiceLogs() {
        return serviceLogService.getAllServiceLogs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceLog> getServiceLogById(@PathVariable Long id) {
        Optional<ServiceLog> log = serviceLogService.getServiceLogById(id);
        return log.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ServiceLog> createServiceLog(@RequestBody ServiceLog serviceLog) {
        return ResponseEntity.ok(serviceLogService.createServiceLog(serviceLog));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ServiceLog> updateServiceLog(@PathVariable Long id, @RequestBody ServiceLog updatedLog) {
        return ResponseEntity.ok(serviceLogService.updateServiceLog(id, updatedLog));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteServiceLog(@PathVariable Long id) {
        serviceLogService.deleteServiceLog(id);
        return ResponseEntity.noContent().build();
    }


}
