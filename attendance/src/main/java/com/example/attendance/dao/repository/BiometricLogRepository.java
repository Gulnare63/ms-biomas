package com.example.attendance.dao.repository;

import com.example.attendance.dao.entity.BiometricLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BiometricLogRepository
        extends JpaRepository<BiometricLogEntity, Long> {

    List<BiometricLogEntity> findByEmployeeIdAndDateTimeBetween(
            Long employeeId,
            LocalDateTime start,
            LocalDateTime end
    );
}
