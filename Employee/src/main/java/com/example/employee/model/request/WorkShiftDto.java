package com.example.employee.model.request;

import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkShiftDto {
    private LocalTime startTime;
    private LocalTime endTime;
}
