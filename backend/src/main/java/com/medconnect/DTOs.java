package com.medconnect;

import com.medconnect.User.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

// Login Request
@Data
class LoginRequest {
    private String email;
    private String password;
}

// Register Request
@Data
class RegisterRequest {
    private String email;
    private String password;
    private String fullName;
    private String phone;
    private Role role;
    private String specialization;
    private String licenseNumber;
}

// Auth Response
@Data
@AllArgsConstructor
class AuthResponse {
    private String token;
    private Long userId;
    private String email;
    private String fullName;
    private Role role;
}

// Appointment Request
@Data
class AppointmentRequest {
    private Long doctorId;
    private LocalDateTime appointmentTime;
    private String reason;
}
