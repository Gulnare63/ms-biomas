package com.example.attendance.service.abstraction;

import com.example.attendance.dao.entity.TotalWorkHourEntity;
import com.example.attendance.model.request.AttendanceStatusDto;

import java.time.LocalDate;

public interface AttendanceService {

    TotalWorkHourEntity calculateTotalWork(Long employeeId, LocalDate date);

    AttendanceStatusDto getAttendanceStatus(Long employeeId, LocalDate date);
}
