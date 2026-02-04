package com.example.employee.dao.repository;

import com.example.employee.dao.entity.EmpPhotoEntity;
import com.example.employee.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeePhotoRepository extends JpaRepository<EmpPhotoEntity, Long> {
    Optional<EmpPhotoEntity> findByEmployeeId(Long employeeId);

    Optional<EmpPhotoEntity> findByEmployeeIdAndStatus(Long employeeId, Status status);

}
