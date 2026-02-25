package com.example.employee.model.request;

import lombok.Data;

@Data
public class EmpCardCreateRequest {
//    @NotBlank
//    @Size(max = 100)
    private String name;

//    @NotBlank
//    @Size(max = 100)
    private String number;
}