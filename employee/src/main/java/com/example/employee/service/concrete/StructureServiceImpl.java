package com.example.employee.service.concrete;

import com.example.employee.dao.entity.StructureEntity;
import com.example.employee.dao.repository.StructureRepository;
import com.example.employee.model.request.StructureSaveRequest;
import com.example.employee.model.response.StructureResponse;
import com.example.employee.service.abstraction.StructureService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StructureServiceImpl implements StructureService {

    private final StructureRepository structureRepository;

    @Override
    public void create(StructureSaveRequest request) {
        StructureEntity parent = resolveParent(request.getParentId());

        StructureEntity entity = StructureEntity.builder()
                .name(request.getName())
                .type(request.getType())
                .parent(parent)
                .build();

        structureRepository.save(entity);
    }

    @Override
    public void update(Long id, StructureSaveRequest request) {
        StructureEntity entity = structureRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Structure not found"));

        StructureEntity parent = resolveParent(request.getParentId());

        entity.setName(request.getName());
        entity.setType(request.getType());
        entity.setParent(parent);

        structureRepository.save(entity);
    }

    @Override
    public StructureResponse getById(Long id) {
        StructureEntity entity = structureRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Structure not found"));

        return toResponse(entity);
    }

    @Override
    public List<StructureResponse> getAll() {
        return structureRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {
        if (!structureRepository.existsById(id)) {
            throw new EntityNotFoundException("Structure not found");
        }
        structureRepository.deleteById(id);
    }

    private StructureEntity resolveParent(Long parentId) {
        if (parentId == null) return null;

        return structureRepository.findById(parentId)
                .orElseThrow(() -> new EntityNotFoundException("Parent structure not found"));
    }

    private StructureResponse toResponse(StructureEntity entity) {
        return StructureResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .type(entity.getType())
                .parentId(entity.getParent() != null ? entity.getParent().getId() : null)
                .build();
    }
}
