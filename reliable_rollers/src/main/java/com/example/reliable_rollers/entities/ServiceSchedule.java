package com.example.reliable_rollers.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(name = "service_schedules")
public class ServiceSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonBackReference
    private Customer customer;

    @NotNull(message = "Garbage pickup day is required")
    @Column(nullable = false)
    private String garbagePickupDay;

    @NotNull(message = "Schedule date is required")
    @Column(nullable = false)
    private LocalDate scheduleDate = LocalDate.now();

    @NotNull(message = "Next pickup date is required")
    @Column(nullable = false)
    private LocalDate nextPickupDate;

    @NotNull(message = "Customer name is required")
    @Column(nullable = false)
    private String customerName;

    @NotNull(message = "Customer address is required")
    @Column(nullable = false)
    private String customerAddress;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        if (customer != null) {
            this.customerName = customer.getFirstName() + " " + customer.getLastName();
            this.customerAddress = customer.getAddress();
        }
    }

    public String getGarbagePickupDay() {
        return garbagePickupDay;
    }

    public void setGarbagePickupDay(String garbagePickupDay) {
        this.garbagePickupDay = garbagePickupDay;
    }

    public LocalDate getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(LocalDate scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public LocalDate getNextPickupDate() {
        return nextPickupDate;
    }

    public void setNextPickupDate(LocalDate nextPickupDate) {
        this.nextPickupDate = nextPickupDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }
}
