package com.example.employee.model.response;

import com.example.employee.model.enums.EmployeeStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmployeeFilterResponse {

    private String name;
    private String surname;
    private String duty;
    private EmployeeStatus status;
}
