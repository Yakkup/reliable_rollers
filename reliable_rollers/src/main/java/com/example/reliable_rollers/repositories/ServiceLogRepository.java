package com.example.reliable_rollers.repositories;

import com.example.reliable_rollers.entities.ServiceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceLogRepository extends JpaRepository<ServiceLog, Long> {
    List<ServiceLog> findByStatus(ServiceLog.ServiceStatus status);
}
