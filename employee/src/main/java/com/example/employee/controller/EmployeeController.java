package com.example.employee.controller;

import com.example.employee.model.request.EmployeFilterRequest;
import com.example.employee.model.request.EmployeeFilterRequest;
import com.example.employee.model.request.EmployeeSaveRequest;
import com.example.employee.model.request.WorkShiftDto;
import com.example.employee.model.response.EmployeeDetailResponse;
import com.example.employee.model.response.EmployeeFilterResponse;
import com.example.employee.model.response.EmployeeListResponse;
import com.example.employee.service.abstraction.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public void create(@RequestBody EmployeeSaveRequest request) {
        employeeService.create(request);
    }

    @GetMapping
    public List<EmployeeListResponse> getAll(EmployeeFilterRequest filter) {
        return employeeService.getAllByFilter(filter);
    }

    @GetMapping("/{id}")
    public EmployeeDetailResponse getById(@PathVariable Long id) {
        return employeeService.getById(id);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id,
                       @RequestBody EmployeeSaveRequest request) {
        employeeService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        employeeService.deleteById(id);
    }

    @GetMapping("/{id}/shift")
    public WorkShiftDto getShift(@PathVariable Long id) {
        return employeeService.getEmployeeShift(id);
    }

    @PutMapping("/edit-status/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void editStatus(@PathVariable Long id,@RequestParam Boolean status) {
        employeeService.editStatus(id, status);
    }

    @GetMapping("/qr/{id}")
    public ResponseEntity<byte[]> getQr(@PathVariable Long id) {
        byte[] qr = employeeService.getQr(id);
        return ResponseEntity.ok()
                .header("Content-Type", "image/png")
                .body(qr);
    }

    @GetMapping("/filter")
    public Page<EmployeeFilterResponse> filter(EmployeFilterRequest request, Pageable pageable) {
        return employeeService.filter(request, pageable);
    }

}
