package com.medconnect;

import com.medconnect.Appointment.Status;
import com.medconnect.User.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

// Auth Service
@Service
class AuthService {
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private AuthenticationManager authenticationManager;
    
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setSpecialization(request.getSpecialization());
        user.setLicenseNumber(request.getLicenseNumber());
        
        user = userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail());
        
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getFullName(), user.getRole());
    }
    
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        String token = jwtUtil.generateToken(user.getEmail());
        
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getFullName(), user.getRole());
    }
}

// Appointment Service
@Service
class AppointmentService {
    @Autowired private AppointmentRepository appointmentRepository;
    @Autowired private UserRepository userRepository;
    
    public Appointment createAppointment(AppointmentRequest request, String patientEmail) {
        User patient = userRepository.findByEmail(patientEmail)
            .orElseThrow(() -> new RuntimeException("Patient not found"));
        
        User doctor = userRepository.findById(request.getDoctorId())
            .orElseThrow(() -> new RuntimeException("Doctor not found"));
        
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setReason(request.getReason());
        appointment.setStatus(Status.SCHEDULED);
        
        return appointmentRepository.save(appointment);
    }
    
    public List<Appointment> getPatientAppointments(String patientEmail) {
        User patient = userRepository.findByEmail(patientEmail)
            .orElseThrow(() -> new RuntimeException("Patient not found"));
        return appointmentRepository.findByPatientOrderByAppointmentTimeDesc(patient);
    }
    
    public List<Appointment> getDoctorAppointments(String doctorEmail) {
        User doctor = userRepository.findByEmail(doctorEmail)
            .orElseThrow(() -> new RuntimeException("Doctor not found"));
        return appointmentRepository.findByDoctorOrderByAppointmentTimeDesc(doctor);
    }
    
    public Appointment updateAppointmentStatus(Long id, Status status, String notes) {
        Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        appointment.setStatus(status);
        if (notes != null) {
            appointment.setNotes(notes);
        }
        
        return appointmentRepository.save(appointment);
    }
    
    public List<User> getAllDoctors() {
        return userRepository.findByRole(Role.DOCTOR);
    }
}

// HealthRecord Service
@Service
class HealthRecordService {
    @Autowired private HealthRecordRepository healthRecordRepository;
    @Autowired private UserRepository userRepository;
    
    private final String uploadDir = "uploads/health-records/";
    
    public HealthRecordService() {
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
    
    public HealthRecord createRecord(String patientEmail, String title, String description, 
                                    HealthRecord.RecordType type, MultipartFile file) throws IOException {
        User patient = userRepository.findByEmail(patientEmail)
            .orElseThrow(() -> new RuntimeException("Patient not found"));
        
        HealthRecord record = new HealthRecord();
        record.setPatient(patient);
        record.setTitle(title);
        record.setDescription(description);
        record.setType(type);
        
        if (file != null && !file.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);
            Files.write(filePath, file.getBytes());
            
            record.setFileName(file.getOriginalFilename());
            record.setFilePath(filePath.toString());
        }
        
        return healthRecordRepository.save(record);
    }
    
    public List<HealthRecord> getPatientRecords(String patientEmail) {
        User patient = userRepository.findByEmail(patientEmail)
            .orElseThrow(() -> new RuntimeException("Patient not found"));
        return healthRecordRepository.findByPatientOrderByRecordDateDesc(patient);
    }
    
    public HealthRecord getRecordById(Long id) {
        return healthRecordRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Record not found"));
    }
}
