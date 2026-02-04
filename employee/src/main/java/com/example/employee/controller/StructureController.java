package com.example.employee.controller;

import com.example.employee.model.request.StructureSaveRequest;
import com.example.employee.model.response.StructureResponse;
import com.example.employee.service.abstraction.StructureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/structures")
@RequiredArgsConstructor
public class StructureController {

    private final StructureService structureService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody StructureSaveRequest request) {
        structureService.create(request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable Long id, @RequestBody StructureSaveRequest request) {
        structureService.update(id, request);
    }

    @GetMapping("/{id}")
    public StructureResponse getById(@PathVariable Long id) {
        return structureService.getById(id);
    }

    @GetMapping
    public List<StructureResponse> getAll() {
        return structureService.getAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        structureService.delete(id);
    }
}
