package com.medconnect;

import com.medconnect.User.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// User Repository
@Repository
interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);
}

// Appointment Repository
@Repository
interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientOrderByAppointmentTimeDesc(User patient);
    List<Appointment> findByDoctorOrderByAppointmentTimeDesc(User doctor);
    List<Appointment> findByDoctorAndAppointmentTimeBetween(User doctor, LocalDateTime start, LocalDateTime end);
}

// HealthRecord Repository
@Repository
interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {
    List<HealthRecord> findByPatientOrderByRecordDateDesc(User patient);
}
