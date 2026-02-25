package com.example.employee.controller;

import com.example.employee.model.request.EmpCardCreateRequest;
import com.example.employee.model.request.EmpCardUpdateRequest;
import com.example.employee.model.response.EmpCardResponse;
import com.example.employee.service.abstraction.EmpCardsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employee/cards")
@RequiredArgsConstructor
public class EmpCardsController {

    private final EmpCardsService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmpCardResponse create(@RequestParam Long employeeId,
                                   @RequestBody EmpCardCreateRequest request) {
        return service.create(employeeId, request);
    }

    @PutMapping("/{cardId}")
    public EmpCardResponse update(@PathVariable Long cardId,
                                   @RequestBody EmpCardUpdateRequest request) {
        return service.update(cardId, request);
    }

    @DeleteMapping("/{cardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long cardId) {
        service.delete(cardId);
    }

    @GetMapping("/{cardId}")
    public EmpCardResponse get(@PathVariable Long cardId) {
        return service.getById(cardId);
    }

    @GetMapping
    public List<EmpCardResponse> getAll(@RequestParam(required = false) Boolean isActive) {
        return service.getAll(isActive);
    }

    @PostMapping("/{cardId}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable Long cardId) {
        service.activate(cardId);
    }

    @PostMapping("/{cardId}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable Long cardId) {
        service.deactivate(cardId);
    }
}