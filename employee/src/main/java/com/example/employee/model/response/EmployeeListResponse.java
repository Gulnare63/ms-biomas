package com.example.employee.model.response;

import lombok.Data;
import lombok.RequiredArgsConstructor;

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

    public EmployeeListResponse(
            Long userId,
            String personalNumber,
            String name,
            String surname,
            String middleName,
            String structureName,
            String duty,
            boolean hasCard,
            boolean hasFace,
            boolean hasFinger
    ) {
        this.userId = userId;
        this.personalNumber = personalNumber;
        this.name = name;
        this.surname = surname;
        this.middleName = middleName;
        this.structureName = structureName;
        this.duty = duty;
        this.hasCard = hasCard;
        this.hasFace = hasFace;
        this.hasFinger = hasFinger;
    }

    public EmployeeListResponse() {
    }
}
