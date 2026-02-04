package com.example.employee.controller;

import com.example.employee.model.request.RegisterFingerRequest;
import com.example.employee.model.response.FingerInfoResponse;
import com.example.employee.service.abstraction.FingerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/fingers")
@RequiredArgsConstructor
public class FingerController {

    private final FingerService fingerService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.OK)
    public void registerFinger(@RequestBody RegisterFingerRequest request) {
        fingerService.registerFinger(request);
    }

    @GetMapping("/employee/{employeeId}")
    public List<FingerInfoResponse> getByEmployee(@PathVariable Long employeeId) {
        return fingerService.getFingers(employeeId);
    }
}
