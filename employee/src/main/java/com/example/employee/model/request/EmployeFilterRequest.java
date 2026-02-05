package com.example.employee.model.request;

import com.example.employee.model.enums.EmployeeStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeFilterRequest {

    private String name;
    private String surname;

    private Long structureId; // dropdown olacaq
    private Long dutyId;      // dropdown olacaq

    private EmployeeStatus status;
}
