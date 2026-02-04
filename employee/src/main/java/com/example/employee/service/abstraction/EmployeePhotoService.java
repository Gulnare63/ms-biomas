package com.example.employee.service.abstraction;


import com.example.employee.model.response.EmployeePhotoResponse;
import org.springframework.web.multipart.MultipartFile;

public interface EmployeePhotoService {
    void uploadOrReplace(Long employeeId, MultipartFile file);
    EmployeePhotoResponse getInfo(Long employeeId);
    void delete(Long employeeId);
}
