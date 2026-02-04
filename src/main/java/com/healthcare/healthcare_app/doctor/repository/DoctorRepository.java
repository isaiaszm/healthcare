package com.healthcare.healthcare_app.doctor.repository;

import com.healthcare.healthcare_app.doctor.entity.Doctor;
import com.healthcare.healthcare_app.enums.Specialization;
import com.healthcare.healthcare_app.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor,Long> {

    Optional<Doctor> findByUser(User user);
    List<Doctor> findBySpecialization(Specialization specialization);
}
