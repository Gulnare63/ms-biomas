package com.example.employee.service.concrete;

import com.example.employee.dao.entity.EmpCardsEntity;
import com.example.employee.dao.entity.EmployeeEntity;
import com.example.employee.dao.repository.EmpCardsRepository;
import com.example.employee.dao.repository.EmployeeRepository;
import com.example.employee.exception.ConflictException;
import com.example.employee.exception.NotFoundException;
import com.example.employee.model.request.EmpCardCreateRequest;
import com.example.employee.model.request.EmpCardUpdateRequest;
import com.example.employee.model.response.EmpCardResponse;
import com.example.employee.service.abstraction.EmpCardsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpCardsServiceImpl implements EmpCardsService {

    private final EmpCardsRepository cardsRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public EmpCardResponse create(Long employeeId, EmpCardCreateRequest request) {
        EmployeeEntity employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("Employee tapılmadı"));

        if (cardsRepository.existsByNumber(request.getNumber())) {
            throw new ConflictException("Kart nömrəsi artıq mövcuddur");
        }

        // istəsən: eyni employee üçün 1 active kart qaydası
        // card create edəndə default false edirik, buna görə conflict eləmirik

        EmpCardsEntity card = EmpCardsEntity.builder()
                .employee(employee)
                .name(request.getName())
                .number(request.getNumber())
                .isActive(false)
                .build();

        return toResponse(cardsRepository.save(card));
    }

    @Override
    public EmpCardResponse update(Long cardId, EmpCardUpdateRequest request) {
        EmpCardsEntity card = cardsRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException("Kart tapılmadı"));

        // bağlıdırsa edit etmə (səndə employee null ola bilməz deyə, bu şərt həmişə true olacaq)
        // Amma praktikada "kart başqa employee-yə bağlıdır" məntiqi lazımdırsa, onu ayrı flow edərik.

        if (!card.getNumber().equals(request.getNumber()) && cardsRepository.existsByNumber(request.getNumber())) {
            throw new ConflictException("Kart nömrəsi artıq mövcuddur");
        }

        card.setName(request.getName());
        card.setNumber(request.getNumber());

        return toResponse(cardsRepository.save(card));
    }

    @Override
    public void delete(Long cardId) {
        EmpCardsEntity card = cardsRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException("Kart tapılmadı"));
        cardsRepository.delete(card);
    }

    @Override
    public EmpCardResponse getById(Long cardId) {
        return toResponse(cardsRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException("Kart tapılmadı")));
    }

    @Override
    public List<EmpCardResponse> getAll(Boolean isActive) {
        List<EmpCardsEntity> list = (isActive == null) ? cardsRepository.findAll() : cardsRepository.findByIsActive(isActive);
        return list.stream().map(this::toResponse).toList();
    }

    @Override
    public void activate(Long cardId) {
        EmpCardsEntity card = cardsRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException("Kart tapılmadı"));

        Long employeeId = card.getEmployee().getId();
        if (cardsRepository.existsByEmployee_IdAndIsActiveTrue(employeeId)) {
            // əgər elə bu kart active-dirsə problem olmasın
            if (Boolean.TRUE.equals(card.getIsActive())) return;
            throw new ConflictException("Əməkdaşın aktiv kartı artıq var.");
        }

        card.setIsActive(true);
        cardsRepository.save(card);
    }

    @Override
    public void deactivate(Long cardId) {
        EmpCardsEntity card = cardsRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException("Kart tapılmadı"));
        card.setIsActive(false);
        cardsRepository.save(card);
    }

    private EmpCardResponse toResponse(EmpCardsEntity c) {
        return EmpCardResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .number(c.getNumber())
                .isActive(c.getIsActive())
                .employeeId(c.getEmployee() != null ? c.getEmployee().getId() : null)
                .build();
    }
}