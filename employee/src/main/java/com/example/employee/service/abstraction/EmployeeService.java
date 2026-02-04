package com.example.employee.service.abstraction;

import com.example.employee.model.request.EmployeeFilterRequest;
import com.example.employee.model.request.EmployeeSaveRequest;
import com.example.employee.model.request.RegisterFingerRequest;
import com.example.employee.model.request.WorkShiftDto;
import com.example.employee.model.response.EmployeeDetailResponse;
import com.example.employee.model.response.EmployeeListResponse;

import java.util.List;

public interface EmployeeService {

    void create(EmployeeSaveRequest request);

    List<EmployeeListResponse> getAllByFilter(EmployeeFilterRequest filter);

    EmployeeDetailResponse getById(Long id);

    void update(Long id, EmployeeSaveRequest request);

    void deleteById(Long id);

    WorkShiftDto getEmployeeShift(Long employeeId);

    void editStatus(Long id, Boolean status);

    byte[] getQr(Long employeeId);


}
