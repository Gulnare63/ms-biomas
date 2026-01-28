package com.example.employee.model.response;

import lombok.Data;

@Data
public class EmployeeListResponse {

    private Long userId;
    private String personalNumber;
    private String name;
    private String surname;
    private String middleName;
    private String structureName;
    private String duty;
    private boolean hasCard;
    private boolean hasFace;
    private boolean hasFinger;
}
