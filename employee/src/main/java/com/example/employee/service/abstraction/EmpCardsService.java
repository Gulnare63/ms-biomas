package com.example.employee.service.abstraction;

import com.example.employee.model.request.EmpCardCreateRequest;
import com.example.employee.model.request.EmpCardUpdateRequest;
import com.example.employee.model.response.EmpCardResponse;

import java.util.List;

public interface EmpCardsService {
    EmpCardResponse create(Long employeeId, EmpCardCreateRequest request);

    EmpCardResponse update(Long cardId, EmpCardUpdateRequest request);

    void delete(Long cardId);

    EmpCardResponse getById(Long cardId);

    List<EmpCardResponse> getAll(Boolean isActive);

    void activate(Long cardId);

    void deactivate(Long cardId);
}