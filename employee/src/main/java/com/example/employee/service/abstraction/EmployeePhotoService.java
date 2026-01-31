package com.example.employee.service.abstraction;


import com.example.employee.model.response.EmployeePhotoResponse;
import org.springframework.web.multipart.MultipartFile;

public interface EmployeePhotoService {
    void uploadOrReplace(Long employeeId, MultipartFile file);   // müəllim: void
    EmployeePhotoResponse getInfo(Long employeeId);              // JSON
    void delete(Long employeeId);                                // void
}
