package com.example.employee.service.abstraction;

import com.example.employee.model.request.StructureSaveRequest;
import com.example.employee.model.response.StructureResponse;

import java.util.List;

public interface StructureService {
    void create(StructureSaveRequest request);
    void update(Long id, StructureSaveRequest request);
    StructureResponse getById(Long id);
    List<StructureResponse> getAll();
    void delete(Long id);
}
