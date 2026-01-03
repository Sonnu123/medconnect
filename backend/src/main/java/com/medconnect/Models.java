package com.medconnect;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

// User Entity
@Data
@Entity
@Table(name = "users")
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String fullName;
    
    @Column(nullable = false)
    private String phone;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    private String specialization;
    private String licenseNumber;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public enum Role {
        PATIENT, DOCTOR, ADMIN
    }
}

// Appointment Entity
@Data
@Entity
@Table(name = "appointments")
class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;
    
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;
    
    @Column(nullable = false)
    private LocalDateTime appointmentTime;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.SCHEDULED;
    
    @Column(length = 1000)
    private String reason;
    
    @Column(length = 2000)
    private String notes;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public enum Status {
        SCHEDULED, COMPLETED, CANCELLED
    }
}

// HealthRecord Entity
@Data
@Entity
@Table(name = "health_records")
class HealthRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;
    
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private User doctor;
    
    @Column(nullable = false)
    private String title;
    
    @Column(length = 2000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecordType type;
    
    private String fileName;
    private String filePath;
    
    @Column(nullable = false)
    private LocalDateTime recordDate = LocalDateTime.now();
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public enum RecordType {
        LAB_RESULT, PRESCRIPTION, XRAY, REPORT, OTHER
    }
}
