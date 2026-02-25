package com.example.employee.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmpCardResponse {
    private Long id;
    private String name;
    private String number;
    private Boolean isActive;
    private Long employeeId;
}