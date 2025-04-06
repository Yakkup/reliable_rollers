package com.example.reliable_rollers.services;

import com.example.reliable_rollers.entities.ServiceLog;
import com.example.reliable_rollers.repositories.ServiceLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServiceLogService {

    @Autowired
    private ServiceLogRepository serviceLogRepository;

    public List<ServiceLog> getAllServiceLogs() {
        return serviceLogRepository.findAll();
    }

    public Optional<ServiceLog> getServiceLogById(Long id) {
        return serviceLogRepository.findById(id);
    }

    public ServiceLog createServiceLog(ServiceLog serviceLog) {
        return serviceLogRepository.save(serviceLog);
    }

    public Map<String, Long> getCompletedServicesPerMonth() {
        List<ServiceLog> completedLogs = serviceLogRepository.findByStatus(ServiceLog.ServiceStatus.COMPLETED);

        return completedLogs.stream()
                .collect(Collectors.groupingBy(
                        log -> log.getServiceDate().getMonth().toString(),
                        Collectors.counting()
                ));
    }

    public ServiceLog updateServiceLog(Long id, ServiceLog updatedLog) {
        return serviceLogRepository.findById(id)
                .map(serviceLog -> {
                    serviceLog.setAction(updatedLog.getAction());
                    serviceLog.setNotes(updatedLog.getNotes());
                    serviceLog.setStatus(updatedLog.getStatus());
                    return serviceLogRepository.save(serviceLog);
                })
                .orElseThrow(() -> new RuntimeException("Service Log not found"));
    }

    public void deleteServiceLog(Long id) {
        if (!serviceLogRepository.existsById(id)) {
            throw new RuntimeException("Service Log not found");
        }
        serviceLogRepository.deleteById(id);
    }
}
