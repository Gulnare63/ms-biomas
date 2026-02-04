package com.example.attendance.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "employee-service",
        url = "http://localhost:8080"
)
public interface EmployeeClient {

    @GetMapping("/v1/employee/{id}/shift")
    WorkShiftDto getEmployeeShift(@PathVariable("id") Long employeeId);
}
