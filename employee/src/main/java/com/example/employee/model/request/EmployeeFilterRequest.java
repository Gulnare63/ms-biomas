package com.example.employee.model.request;

import lombok.Data;

@Data
public class EmployeeFilterRequest {

    private String personalNumber;
    private String name;
    private String surname;
    private String middleName;
    private Long structureId;
}
