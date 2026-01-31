package com.example.employee.controller;

import com.example.employee.model.response.EmployeePhotoResponse;
import com.example.employee.service.abstraction.EmployeePhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/employees/{employeeId}/photo")
public class EmployeeImageController {

    private final EmployeePhotoService employeePhotoService;

    // müəllim: request-də employeeId göstər, method void, sadəcə status code
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadOrReplace(
            @PathVariable Long employeeId,
            @RequestPart("file") MultipartFile file
    ) {
        employeePhotoService.uploadOrReplace(employeeId, file);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmployeePhotoResponse> getInfo(@PathVariable Long employeeId) {
        return ResponseEntity.ok(employeePhotoService.getInfo(employeeId));
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@PathVariable Long employeeId) {
        employeePhotoService.delete(employeeId);
        return ResponseEntity.noContent().build();
    }
}
