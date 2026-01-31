package com.example.employee.model.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class EmployeeWorkShiftResponse {
    private LocalTime startTime;
    private LocalTime endTime;
}
