package com.example.employee.model.request;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class EmployeeUpdatedEvent {
    private Long employeeId;
    private String personalCode;
    private String name;
    private String surname;
    private String middleName;
    private Long structureId;
    private Long dutyId;
    private String status;
    private Boolean isActive;
}