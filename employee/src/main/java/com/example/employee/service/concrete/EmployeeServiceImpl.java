package com.example.employee.service.concrete;

import com.example.employee.dao.entity.DutyEntity;
import com.example.employee.dao.entity.EmployeeEntity;
import com.example.employee.dao.entity.OutboxEventEntity;
import com.example.employee.dao.entity.StructureEntity;
import com.example.employee.dao.repository.DutyRepository;
import com.example.employee.dao.repository.EmployeeRepository;
import com.example.employee.dao.repository.OutboxEventRepository;
import com.example.employee.dao.repository.StructureRepository;
import com.example.employee.mapper.ManualEmployeeMapper;
import com.example.employee.model.dto.EmployeeCreatedEvent;
import com.example.employee.model.enums.EmployeeStatus;
import com.example.employee.model.enums.OutboxStatus;
import com.example.employee.model.enums.Status;
import com.example.employee.model.request.EmployeFilterRequest;
import com.example.employee.model.request.EmployeeFilterRequest;
import com.example.employee.model.request.EmployeeSaveRequest;
import com.example.employee.model.request.EmployeeUpdatedEvent;
import com.example.employee.model.request.WorkShiftDto;
import com.example.employee.model.response.EmployeeDetailResponse;
import com.example.employee.model.response.EmployeeFilterResponse;
import com.example.employee.model.response.EmployeeListResponse;
import com.example.employee.service.abstraction.EmployeeService;
import com.example.employee.service.abstraction.QrCodeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final StructureRepository structureRepository;
    private final ManualEmployeeMapper mapper;
    private final QrCodeService qrCodeService;
    private  final DutyRepository dutyRepository;
    private  final OutboxEventRepository outboxEventRepository;
    private  final ObjectMapper objectMapper;

    @Transactional
    public void create(EmployeeSaveRequest request) {
        // optional: personalCode unikaldırsa əvvəl yoxla
        employeeRepository.findByPersonalCode(request.getPersonalCode())
                .ifPresent(e -> { throw new IllegalStateException("Employee already exists: " + request.getPersonalCode()); });

        StructureEntity structure = structureRepository.findById(request.getStructureId())
                .orElseThrow(() -> new EntityNotFoundException("Structure not found"));

        DutyEntity duty = dutyRepository.findById(request.getDutyId())
                .orElseThrow(() -> new EntityNotFoundException("Duty not found"));

        EmployeeEntity employee = mapper.toEntity(request, structure, duty);
        employeeRepository.save(employee);

        EmployeeCreatedEvent payload = EmployeeCreatedEvent.builder()
                .employeeId(employee.getId())
                .personalCode(employee.getPersonalCode())
                .name(employee.getName())
                .surname(employee.getSurname())
                .middleName(employee.getMiddleName())
                .structureId(structure.getId())
                .dutyId(duty.getId())
                .status(employee.getStatus().name())
                .isActive(employee.getIsActive())
                .build();

        String payloadJson;
        try {
            payloadJson = objectMapper.writeValueAsString(payload);
        } catch (Exception ex) {
            // burada exception atmaq daha doğrudur ki transaction rollback olsun
            throw new IllegalStateException("Cannot serialize outbox payload", ex);
        }

        OutboxEventEntity outbox = OutboxEventEntity.builder()
                .id(UUID.randomUUID())
                .aggregateType("EMPLOYEE")
                .aggregateId(String.valueOf(employee.getId()))
                .eventType("EMPLOYEE_CREATED")
                .payload(payloadJson)
                .status(OutboxStatus.PENDING)
                .attemptCount(0)
                .nextRetryAt(LocalDateTime.now())
                .build();

        outboxEventRepository.save(outbox);
    }
//    @Override
//    public void create(EmployeeSaveRequest request) {
//        StructureEntity structure = structureRepository.findById(request.getStructureId())
//                .orElseThrow(() -> new EntityNotFoundException("Structure not found"));
//
//        DutyEntity duty = dutyRepository.findById(request.getDutyId())
//                .orElseThrow(() -> new EntityNotFoundException("duty not found"));
//
//        EmployeeEntity employee = mapper.toEntity(request, structure, duty);
//        employeeRepository.save(employee);
//
//
//    }
//
//    @Override
//    public List<EmployeeListResponse> getAllByFilter(EmployeeFilterRequest filter) {
//        Specification<EmployeeEntity> spec = (root, query, cb) -> {
//            var predicates = cb.conjunction();
//
//            if (filter.getPersonalNumber() != null)
//                predicates.getExpressions().add(
//                        cb.equal(root.get("personalCode"), filter.getPersonalNumber()));
//
//            if (filter.getName() != null)
//                predicates.getExpressions().add(
//                        cb.like(cb.lower(root.get("name")),
//                                "%" + filter.getName().toLowerCase() + "%"));
//
//            if (filter.getStructureId() != null)
//                predicates.getExpressions().add(
//                        cb.equal(root.get("structure").get("id"), filter.getStructureId()));
//
//            return predicates;
//        };
//
//        return employeeRepository.findAll(spec)
//                .stream()
//                .map(mapper::toListResponse)
//                .toList();
//    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeListResponse> getAllByFilter(EmployeeFilterRequest filter) {

        String personal = (filter.getPersonalNumber() == null || filter.getPersonalNumber().isBlank())
                ? null : filter.getPersonalNumber();

        String name = (filter.getName() == null || filter.getName().isBlank())
                ? null : filter.getName();

        Long structureId = filter.getStructureId();

        return employeeRepository.findAllForList(personal, name, structureId);
    }
    @Override
    @Transactional(readOnly = true)
    public EmployeeDetailResponse getById(Long id) {
        EmployeeEntity employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        return mapper.toDetailResponse(employee);
    }
    @Override
    @Transactional
    public void update(Long id, EmployeeSaveRequest request) {
        EmployeeEntity employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        StructureEntity structure = structureRepository.findById(request.getStructureId())
                .orElseThrow(() -> new EntityNotFoundException("Structure not found"));

        DutyEntity duty = dutyRepository.findById(request.getDutyId())
                .orElseThrow(() -> new EntityNotFoundException("Duty not found"));

        mapper.updateEntity(employee, request, structure, duty);
        employeeRepository.save(employee);

        // Outbox payload
        EmployeeUpdatedEvent payload = EmployeeUpdatedEvent.builder()
                .employeeId(employee.getId())
                .personalCode(employee.getPersonalCode())
                .name(employee.getName())
                .surname(employee.getSurname())
                .middleName(employee.getMiddleName())
                .structureId(structure.getId())
                .dutyId(duty.getId())
                .status(employee.getStatus().name())
                .isActive(employee.getIsActive())
                .build();

        String payloadJson;
        try {
            payloadJson = objectMapper.writeValueAsString(payload);
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot serialize outbox payload", ex);
        }

        OutboxEventEntity outbox = OutboxEventEntity.builder()
                .id(UUID.randomUUID())
                .aggregateType("EMPLOYEE")
                .aggregateId(String.valueOf(employee.getId()))
                .eventType("EMPLOYEE_UPDATED")
                .payload(payloadJson)
                .status(OutboxStatus.PENDING)
                .attemptCount(0)
                .nextRetryAt(LocalDateTime.now())
                .build();

        outboxEventRepository.save(outbox);
    }
//    @Override
//    public void update(Long id, EmployeeSaveRequest request) {
//        EmployeeEntity employee = employeeRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));
//
//        StructureEntity structure = structureRepository.findById(request.getStructureId())
//                .orElseThrow(() -> new EntityNotFoundException("Structure not found"));
//        DutyEntity duty = dutyRepository.findById(request.getDutyId())
//                .orElseThrow(() -> new EntityNotFoundException("duty not found"));
//
//        mapper.updateEntity(employee, request, structure,duty);
//        employeeRepository.save(employee);
//    }

    @Override
    public void deleteById(Long id) {
        employeeRepository.deleteById(id);
    }

    @Override
    public WorkShiftDto getEmployeeShift(Long employeeId) {
        EmployeeEntity employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        return WorkShiftDto.builder()
                .startTime(employee.getWorkShift().getStartTime())
                .endTime(employee.getWorkShift().getEndTime())
                .build();
    }

    @Override
    public void editStatus(Long id, EmployeeStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status must not be null");
        }

        EmployeeEntity employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        employee.setStatus(status);
        employeeRepository.save(employee);
    }

    @Override
    public byte[] getQr(Long employeeId) {
        EmployeeEntity employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        return qrCodeService.generateEmployeeQr(employee.getId());
    }

    @Override
    public Page<EmployeeFilterResponse> filter(EmployeFilterRequest filter, Pageable pageable) {

        Specification<EmployeeEntity> spec = (root, query, cb) -> {
            var p = cb.conjunction();

            if (filter.getName() != null && !filter.getName().isBlank()) {
                p.getExpressions().add(
                        cb.like(cb.lower(root.get("name")),
                                "%" + filter.getName().toLowerCase() + "%")
                );
            }

            if (filter.getSurname() != null && !filter.getSurname().isBlank()) {
                p.getExpressions().add(
                        cb.like(cb.lower(root.get("surname")),
                                "%" + filter.getSurname().toLowerCase() + "%")
                );
            }

            if (filter.getStructureId() != null) {
                p.getExpressions().add(
                        cb.equal(root.get("structure").get("id"), filter.getStructureId())
                );
            }

            if (filter.getDutyId() != null) {
                p.getExpressions().add(
                        cb.equal(root.get("duty").get("id"), filter.getDutyId())
                );
            }

            if (filter.getStatus() != null) {
                p.getExpressions().add(
                        cb.equal(root.get("status"), filter.getStatus())
                );
            }

            return p;
        };

        return employeeRepository.findAll(spec, pageable)
                .map(e -> EmployeeFilterResponse.builder()
                        .name(e.getName())
                        .surname(e.getSurname())
                        .duty(e.getDuty() != null ? e.getDuty().getName() : null)
                        .status(e.getStatus())
                        .build());
    }
}
