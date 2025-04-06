package com.example.reliable_rollers.repositories;

import com.example.reliable_rollers.entities.ServiceSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ServiceScheduleRepository extends JpaRepository<ServiceSchedule, Long> {
    Optional<ServiceSchedule> findByCustomerId(Long customerId);
    List<ServiceSchedule> findByNextPickupDate(LocalDate date);
    List<ServiceSchedule> findByNextPickupDateBetween(LocalDate startDate, LocalDate endDate);
}

