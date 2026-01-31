package com.example.attendance.controller;

import com.example.attendance.dao.entity.TotalWorkHourEntity;
import com.example.attendance.model.request.AttendanceStatusDto;
import com.example.attendance.service.abstraction.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping("/total-work")
    public TotalWorkHourEntity getTotalWork(
            @RequestParam Long employeeId,
            @RequestParam LocalDate date
    ) {
        return attendanceService.calculateTotalWork(employeeId, date);
    }

    @GetMapping("/status")
    public AttendanceStatusDto getAttendanceStatus(
            @RequestParam Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return attendanceService.getAttendanceStatus(employeeId, date);
    }
}
