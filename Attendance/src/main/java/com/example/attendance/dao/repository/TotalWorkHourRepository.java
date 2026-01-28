package com.example.attendance.dao.repository;


import com.example.attendance.dao.entity.TotalWorkHourEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface TotalWorkHourRepository
        extends JpaRepository<TotalWorkHourEntity, Long> {

    Optional<TotalWorkHourEntity> findByEmployeeIdAndWorkDate(
            Long employeeId,
            LocalDate workDate
    );
}
