package com.example.attendance.dao.repository;

import com.example.attendance.dao.entity.BiometricLogEntity;

import com.example.attendance.model.response.AttendanceLogRow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDateTime;
import java.util.List;

public interface BiometricLogRepository extends JpaRepository<BiometricLogEntity, Long>{

    List<BiometricLogEntity> findByEmployeeIdAndDateTimeBetween(
            Long employeeId,
            LocalDateTime start,
            LocalDateTime end
    );
    @Query(
            value = """
    SELECT
        e.name       AS name,
        e.surname    AS surname,
        bl.verify    AS verify,
        bl.date_time AS "dateTime",
        bl.in_out    AS "inOut",
        dv.name      AS "deviceName",
        dp.name      AS "devicePlaceName"
    FROM biometric_log bl
    JOIN employeee e     ON e.id = bl.employee_id
    JOIN devices dv      ON dv.id = bl.device_id
    JOIN device_place dp ON dp.id = dv.device_place_id
    WHERE
        (:name IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:surname IS NULL OR LOWER(e.surname) LIKE LOWER(CONCAT('%', :surname, '%')))
        AND (:structureId IS NULL OR e.structure_id = :structureId)
        AND (:devicePlaceId IS NULL OR dv.device_place_id = :devicePlaceId)
        AND (:startTime IS NULL OR bl.date_time >= :startTime)
        AND (:endTime IS NULL OR bl.date_time <= :endTime)
    ORDER BY bl.date_time DESC
  """,
            countQuery = """
    SELECT COUNT(*)
    FROM biometric_log bl
    JOIN employeee e     ON e.id = bl.employee_id
    JOIN devices dv      ON dv.id = bl.device_id
    JOIN device_place dp ON dp.id = dv.device_place_id
    WHERE
        (:name IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:surname IS NULL OR LOWER(e.surname) LIKE LOWER(CONCAT('%', :surname, '%')))
        AND (:structureId IS NULL OR e.structure_id = :structureId)
        AND (:devicePlaceId IS NULL OR dv.device_place_id = :devicePlaceId)
        AND (:startTime IS NULL OR bl.date_time >= :startTime)
        AND (:endTime IS NULL OR bl.date_time <= :endTime)
  """,
            nativeQuery = true
    )
    Page<AttendanceLogRow> filter(
            @Param("name") String name,
            @Param("surname") String surname,
            @Param("structureId") Long structureId,
            @Param("devicePlaceId") Long devicePlaceId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable
    );
}
