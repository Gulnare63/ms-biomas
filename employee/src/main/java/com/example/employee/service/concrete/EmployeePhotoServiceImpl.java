package com.example.employee.service.concrete;

import com.example.employee.dao.entity.EmpPhotoEntity;
import com.example.employee.dao.entity.EmployeeEntity;
import com.example.employee.dao.repository.EmployeePhotoRepository;
import com.example.employee.dao.repository.EmployeeRepository;
import com.example.employee.exception.BadRequestException;
import com.example.employee.exception.NotFoundException;
import com.example.employee.model.enums.Status;
import com.example.employee.model.response.EmployeePhotoResponse;
import com.example.employee.service.abstraction.EmployeePhotoService;
import com.example.employee.service.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeePhotoServiceImpl implements EmployeePhotoService {

    private static final long MAX_SIZE_BYTES = 200 * 1024;
    private static final Set<String> ALLOWED = Set.of("image/jpeg", "image/png");

    private final EmployeePhotoRepository photoRepository;
    private final EmployeeRepository employeeRepository;
    private final StorageService storageService;

    @Override
    @Transactional
    public void uploadOrReplace(Long employeeId, MultipartFile file) {
        validate(employeeId, file);

        EmployeeEntity employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("Employee not found"));

        String folder = "employees/" + employeeId + "/photo";
        String ext = "image/png".equals(file.getContentType()) ? ".png" : ".jpg";
        String objectName = UUID.randomUUID() + ext;

        EmpPhotoEntity entity = photoRepository.findByEmployeeId(employeeId)
                .orElse(null);

        if (entity != null) {
            try {
                if (StringUtils.hasText(entity.getFolder()) && StringUtils.hasText(entity.getObjectName())) {
                    storageService.delete(entity.getFolder(), entity.getObjectName());
                }
            } catch (Exception ignored) {
            }
        }

        storageService.upload(folder, objectName, file);

        if (entity == null) {
            entity = EmpPhotoEntity.builder()
                    .employee(employee)
                    .createdAt(OffsetDateTime.now())
                    .build();
        }

        entity.setFolder(folder);
        entity.setObjectName(objectName);
        entity.setContentType(file.getContentType());
        entity.setSizeBytes(file.getSize());
        entity.setStatus(Status.ACTIVE);

        entity.setCreatedAt(OffsetDateTime.now());

        photoRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeePhotoResponse getInfo(Long employeeId) {
        EmpPhotoEntity photo = photoRepository
                .findByEmployeeIdAndStatus(employeeId, Status.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Employee photo not found"));

        String url = storageService.generateUrl(photo.getFolder(), photo.getObjectName());
        return new EmployeePhotoResponse(employeeId, url);
    }

    @Override
    @Transactional
    public void delete(Long employeeId) {
        EmpPhotoEntity photo = photoRepository
                .findByEmployeeIdAndStatus(employeeId, Status.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Employee photo not found"));

        storageService.delete(photo.getFolder(), photo.getObjectName());

        photo.setStatus(Status.DELETED);
        photoRepository.save(photo);
    }

    private void validate(Long employeeId, MultipartFile file) {
        if (employeeId == null || employeeId <= 0) {
            throw new BadRequestException("Invalid employeeId");
        }
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is required");
        }
        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new BadRequestException("Max file size is 200KB");
        }
        String ct = file.getContentType();
        if (!StringUtils.hasText(ct) || !ALLOWED.contains(ct)) {
            throw new BadRequestException("Only jpeg/png allowed");
        }
    }
}
