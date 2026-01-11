package com.example.MedicalApplication.repository;

import com.example.MedicalApplication.model.DoctorPatient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorPatientRepository extends JpaRepository<DoctorPatient, Long> {

    List<DoctorPatient> findByDoctorId(Long doctorId);

    boolean existsByDoctorIdAndPatientId(Long doctorId, Long patientId);

    Optional<DoctorPatient> findByDoctorIdAndPatientId(Long doctorId, Long patientId);
}
