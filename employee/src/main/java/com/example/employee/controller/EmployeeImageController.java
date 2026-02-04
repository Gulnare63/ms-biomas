package com.example.employee.controller;

import com.example.employee.model.response.EmployeePhotoResponse;
import com.example.employee.service.abstraction.EmployeePhotoService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/employees")
public class EmployeeImageController {

    private final EmployeePhotoService employeePhotoService;

    @PutMapping(value = "/{employeeId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadOrReplace(
            @PathVariable Long employeeId,
            @RequestParam("file")  MultipartFile file
    ) {
        employeePhotoService.uploadOrReplace(employeeId, file);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{employeeId}/photo")
    public ResponseEntity<EmployeePhotoResponse> getInfo(
            @PathVariable  Long employeeId
    ) {
        return ResponseEntity.ok(employeePhotoService.getInfo(employeeId));
    }

    @DeleteMapping("/{employeeId}/photo")
    public ResponseEntity<Void> delete(
            @PathVariable Long employeeId
    ) {
        employeePhotoService.delete(employeeId);
        return ResponseEntity.noContent().build();
    }
}