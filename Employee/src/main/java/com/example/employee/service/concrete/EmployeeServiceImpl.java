package com.example.employee.service.concrete;

import com.example.employee.dao.entity.EmployeeEntity;
import com.example.employee.dao.entity.StructureEntity;
import com.example.employee.dao.entity.WorkShiftEntity;
import com.example.employee.dao.repository.EmployeeRepository;
import com.example.employee.dao.repository.StructureRepository;
import com.example.employee.mapper.ManualEmployeeMapper;
import com.example.employee.model.request.EmployeeFilterRequest;
import com.example.employee.model.request.EmployeeSaveRequest;
import com.example.employee.model.request.WorkShiftDto;
import com.example.employee.model.response.EmployeeDetailResponse;
import com.example.employee.model.response.EmployeeListResponse;
import com.example.employee.model.response.EmployeeWorkShiftResponse;
import com.example.employee.service.abstraction.EmployeeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final StructureRepository structureRepository;

    private final ManualEmployeeMapper mapper = new ManualEmployeeMapper();

    @Override
    public void create(EmployeeSaveRequest request) {
        StructureEntity structure = structureRepository.findById(request.getStructureId())
                .orElseThrow(() -> new EntityNotFoundException("Structure not found"));

        EmployeeEntity employee = mapper.toEntity(request, structure);
        employeeRepository.save(employee);
    }

    @Override
    public List<EmployeeListResponse> getAllByFilter(EmployeeFilterRequest filter) {
        Specification<EmployeeEntity> spec = (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filter.getPersonalNumber() != null)
                predicates.getExpressions().add(
                        cb.equal(root.get("personalCode"), filter.getPersonalNumber()));

            if (filter.getName() != null)
                predicates.getExpressions().add(
                        cb.like(cb.lower(root.get("name")),
                                "%" + filter.getName().toLowerCase() + "%"));

            if (filter.getStructureId() != null)
                predicates.getExpressions().add(
                        cb.equal(root.get("structure").get("id"), filter.getStructureId()));

            return predicates;
        };

        return employeeRepository.findAll(spec)
                .stream()
                .map(mapper::toListResponse)
                .toList();
    }

    @Override
    public EmployeeDetailResponse getById(Long id) {
        EmployeeEntity employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        return mapper.toDetailResponse(employee);
    }

    @Override
    public void update(Long id, EmployeeSaveRequest request) {
        EmployeeEntity employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        StructureEntity structure = structureRepository.findById(request.getStructureId())
                .orElseThrow(() -> new EntityNotFoundException("Structure not found"));

        mapper.updateEntity(employee, request, structure);
        employeeRepository.save(employee);
    }

    @Override
    public void deleteById(Long id) {
        employeeRepository.deleteById(id);
    }



//    @Override
//    public EmployeeWorkShiftResponse getWorkShiftByDate(Long employeeId, LocalDate date) {
//
//        WorkShiftEntity shift = employeeRepository
//                .findWorkShiftByEmployeeIdAndDate(employeeId, date);
//        // (burada tarixə görə smena tapılır – real biznes logic)
//
//        EmployeeWorkShiftResponse response = new EmployeeWorkShiftResponse();
//        response.setStartTime(shift.getStartTime());
//        response.setEndTime(shift.getEndTime());
//        return response;
//    }
@Override
public WorkShiftDto getEmployeeShift(Long employeeId) {
    EmployeeEntity employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));

    return WorkShiftDto.builder()
            .startTime(employee.getWorkShift().getStartTime())
            .endTime(employee.getWorkShift().getEndTime())
            .build();
}

}
