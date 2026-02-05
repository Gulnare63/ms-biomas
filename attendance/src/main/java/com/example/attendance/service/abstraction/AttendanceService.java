package com.example.attendance.service.abstraction;

import com.example.attendance.dao.entity.TotalWorkHourEntity;

import com.example.attendance.model.request.AttendanceFilterRequest;
import com.example.attendance.model.request.AttendanceStatusDto;
import com.example.attendance.model.response.AttendanceLogRow;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;

public interface AttendanceService {

    TotalWorkHourEntity calculateTotalWork(Long employeeId, LocalDate date);

    AttendanceStatusDto getAttendanceStatus(Long employeeId, LocalDate date);

    Page<AttendanceLogRow> filter(
            AttendanceFilterRequest request,
            Pageable pageable
    );
}
