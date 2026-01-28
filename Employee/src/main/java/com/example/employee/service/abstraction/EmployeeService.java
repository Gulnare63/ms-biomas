package com.example.employee.service.abstraction;


import com.example.employee.model.request.EmployeeFilterRequest;
import com.example.employee.model.request.EmployeeSaveRequest;
import com.example.employee.model.request.WorkShiftDto;
import com.example.employee.model.response.EmployeeDetailResponse;
import com.example.employee.model.response.EmployeeListResponse;
import com.example.employee.model.response.EmployeeWorkShiftResponse;

import java.time.LocalDate;
import java.util.List;

public interface EmployeeService {

    void create(EmployeeSaveRequest request);

    List<EmployeeListResponse> getAllByFilter(EmployeeFilterRequest filter);

    EmployeeDetailResponse getById(Long id);

    void update(Long id, EmployeeSaveRequest request);

    void deleteById(Long id);

//    EmployeeWorkShiftResponse getWorkShiftByDate(Long employeeId, LocalDate date);

    WorkShiftDto getEmployeeShift(Long employeeId);




}
