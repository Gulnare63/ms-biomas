package com.example.employee.service.abstraction;

import com.example.employee.dao.entity.EmpFingersEntity;
import com.example.employee.model.request.FingerUpsertRequest;
import com.example.employee.model.request.RegisterFingerRequest;
import com.example.employee.model.response.FingerInfoResponse;

import java.util.List;
import java.util.Optional;

public interface FingerService {
 void registerFinger(RegisterFingerRequest request);
    List<FingerInfoResponse> getFingers(Long employeeId);


    // CRUD: DB-yə birbaşa yazmaq istəsən
    void upsertFinger(FingerUpsertRequest request);


    // READ single
    FingerInfoResponse getFinger(Long fingerId);

    // DELETE by id
    void deleteFinger(Long fingerId);

    // DELETE by employee + index + hand
    void deleteFinger(Long employeeId, Integer fingerIndex, String hand);
}
