package com.example.employee.model.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EmployeeWorkShiftRequest {
    private Long employeeId;
    private LocalDate date;
}
