package com.example.attendance.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceFilterResponse {

    private Long id;
    private Long employeeId;
    private String employeeName;
    private String employeeSurname;
    private String structureName;
    private String dutyName;
    private String deviceName;
    private LocalDateTime dateTime;
    private String verifyType;
    private String inOutType;
}
