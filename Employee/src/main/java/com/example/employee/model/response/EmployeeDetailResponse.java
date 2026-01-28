package com.example.employee.model.response;

import lombok.Data;
import java.util.List;

@Data
public class EmployeeDetailResponse {

    private Long userId;
    private String personalNumber;
    private String name;
    private String surname;
    private String middleName;

    private Object card;
    private List<FingerprintResponse> employeeFingers;
    private List<FingerDataResponse> employeeFingerDataList;
    private String face;

    private String structureName;
    private String duty;
}
