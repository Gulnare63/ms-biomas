package com.example.attendance.model.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AttendanceFilterRequest {
    private String name;
    private String surname;
    private Long structureId;     // dropdown
    private Long devicePlaceId;   // dropdown
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
