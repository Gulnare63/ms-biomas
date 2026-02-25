package com.example.employee.model.request;

import com.example.employee.model.enums.HandType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FingerUpsertRequest {

//    @NotNull
    private Long employeeId;

//    @NotNull
//    @Min(1) @Max(5)
    private Integer fingerIndex;

//    @NotNull
    private HandType hand;

//    @NotBlank
    private String fingerprintBase64;
}