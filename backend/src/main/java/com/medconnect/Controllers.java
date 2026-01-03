package com.medconnect;

import com.medconnect.Appointment.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Map;

// Auth Controller
@RestController
@RequestMapping("/api/auth")
class AuthController {
    @Autowired private AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}

// Appointment Controller
@RestController
@RequestMapping("/api/appointments")
class AppointmentController {
    @Autowired private AppointmentService appointmentService;
    
    @PostMapping
    public ResponseEntity<Appointment> createAppointment(@RequestBody AppointmentRequest request,
                                                         Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(appointmentService.createAppointment(request, email));
    }
    
    @GetMapping("/patient")
    public ResponseEntity<List<Appointment>> getPatientAppointments(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(appointmentService.getPatientAppointments(email));
    }
    
    @GetMapping("/doctor")
    public ResponseEntity<List<Appointment>> getDoctorAppointments(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(appointmentService.getDoctorAppointments(email));
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<Appointment> updateStatus(@PathVariable Long id,
                                                    @RequestBody Map<String, String> body) {
        Status status = Status.valueOf(body.get("status"));
        String notes = body.get("notes");
        return ResponseEntity.ok(appointmentService.updateAppointmentStatus(id, status, notes));
    }
    
    @GetMapping("/doctors")
    public ResponseEntity<List<User>> getAllDoctors() {
        return ResponseEntity.ok(appointmentService.getAllDoctors());
    }
}

// HealthRecord Controller
@RestController
@RequestMapping("/api/health-records")
class HealthRecordController {
    @Autowired private HealthRecordService healthRecordService;
    
    @PostMapping
    public ResponseEntity<HealthRecord> createRecord(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("type") HealthRecord.RecordType type,
            @RequestParam(value = "file", required = false) MultipartFile file,
            Authentication authentication) throws IOException {
        
        String email = authentication.getName();
        return ResponseEntity.ok(healthRecordService.createRecord(email, title, description, type, file));
    }
    
    @GetMapping
    public ResponseEntity<List<HealthRecord>> getRecords(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(healthRecordService.getPatientRecords(email));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<HealthRecord> getRecord(@PathVariable Long id) {
        return ResponseEntity.ok(healthRecordService.getRecordById(id));
    }
}
