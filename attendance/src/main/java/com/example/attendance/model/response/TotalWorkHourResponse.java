package com.example.attendance.model.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class TotalWorkHourResponse {

    private Long employeeId;
    private LocalDate workDate;

    private LocalDateTime workStartDate;
    private LocalDateTime workEndDate;

    private LocalDateTime firstIn;
    private LocalDateTime lastOut;

    private Long totalWorkMinutes;
}
