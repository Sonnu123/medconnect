package com.medconnect;

import com.medconnect.User.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User doctor = new User();
            doctor.setEmail("doctor@medconnect.com");
            doctor.setPassword(passwordEncoder.encode("doctor123"));
            doctor.setFullName("Dr. Sarah Johnson");
            doctor.setPhone("555-0101");
            doctor.setRole(Role.DOCTOR);
            doctor.setSpecialization("General Medicine");
            doctor.setLicenseNumber("MD123456");
            userRepository.save(doctor);
            
            User patient = new User();
            patient.setEmail("patient@medconnect.com");
            patient.setPassword(passwordEncoder.encode("patient123"));
            patient.setFullName("John Smith");
            patient.setPhone("555-0102");
            patient.setRole(Role.PATIENT);
            userRepository.save(patient);
            
            System.out.println("✓ Default users created");
        }
    }
}
