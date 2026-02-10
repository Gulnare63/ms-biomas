package com.example.employee.model.request;

import com.example.employee.model.enums.HandType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterFingerRequest {

    private Long employeeId;
    private Integer fingerIndex;   // 1–5
    private HandType hand;
    private Long enrollDeviceId;
}
