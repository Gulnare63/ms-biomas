package com.example.employee.service.abstraction;

import com.example.employee.model.request.RegisterFingerRequest;
import com.example.employee.model.response.FingerInfoResponse;

import java.util.List;

public interface FingerService {
 void registerFinger(RegisterFingerRequest request);
    List<FingerInfoResponse> getFingers(Long employeeId);
}
