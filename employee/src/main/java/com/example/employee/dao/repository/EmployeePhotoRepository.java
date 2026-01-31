package com.example.employee.dao.repository;

import com.example.employee.dao.entity.EmpPhotoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeePhotoRepository extends JpaRepository<EmpPhotoEntity, Long> {

    Optional<EmpPhotoEntity> findByEmployeeIdAndStatus(Long employeeId, EmpPhotoEntity.Status status);

}
