package com.example.employee.mapper;

import com.example.employee.dao.entity.*;

import com.example.employee.model.request.EmployeeSaveRequest;
import com.example.employee.model.response.EmployeeDetailResponse;
import com.example.employee.model.response.EmployeeListResponse;
import com.example.employee.model.response.FingerprintResponse;
import com.example.employee.service.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ManualEmployeeMapper {

    private final StorageService storageService;

    public EmployeeListResponse toListResponse(EmployeeEntity employee) {
        if (employee == null) return null;

        EmployeeListResponse response = new EmployeeListResponse();
        response.setUserId(employee.getId());
        response.setPersonalNumber(nvl(employee.getPersonalCode()));
        response.setName(nvl(employee.getName()));
        response.setSurname(nvl(employee.getSurname()));
        response.setMiddleName(nvl(employee.getMiddleName()));
        response.setDuty(nvl(employee.getDuty()));
        response.setStructureName(employee.getStructure() != null ? nvl(employee.getStructure().getName()) : "");
        response.setHasCard(employee.getCards() != null && !employee.getCards().isEmpty());

        // Face yalnız ACTIVE olanda true olsun (yoxsa DELETED olsa da entity qalır)
        EmpPhotoEntity photo = employee.getPhoto();
        response.setHasFace(photo != null && photo.getStatus() == EmpPhotoEntity.Status.ACTIVE);

        response.setHasFinger(employee.getFingers() != null && !employee.getFingers().isEmpty());
        return response;
    }

    public EmployeeDetailResponse toDetailResponse(EmployeeEntity employee) {
        if (employee == null) return null;

        EmployeeDetailResponse response = new EmployeeDetailResponse();
        response.setUserId(employee.getId());
        response.setPersonalNumber(nvl(employee.getPersonalCode()));
        response.setName(nvl(employee.getName()));
        response.setSurname(nvl(employee.getSurname()));
        response.setMiddleName(nvl(employee.getMiddleName()));
        response.setDuty(nvl(employee.getDuty()));
        response.setStructureName(employee.getStructure() != null ? nvl(employee.getStructure().getName()) : "");

        // ✅ əvvəllər filename qaytarırdın, indi URL qaytarırıq
        EmpPhotoEntity photo = employee.getPhoto();
        if (photo != null && photo.getStatus() == EmpPhotoEntity.Status.ACTIVE) {
            String url = storageService.generateUrl(photo.getFolder(), photo.getObjectName());
            response.setFace(url);
        } else {
            response.setFace(null);
        }

        response.setCard(employee.getCards() != null && !employee.getCards().isEmpty()
                ? employee.getCards().get(0)
                : null);

        response.setEmployeeFingers(mapFingers(employee.getFingers()));
        response.setEmployeeFingerDataList(new ArrayList<>());

        return response;
    }

    public EmployeeEntity toEntity(EmployeeSaveRequest request, StructureEntity structure) {
        if (request == null) return null;

        EmployeeEntity employee = new EmployeeEntity();
        employee.setPersonalCode(nvl(request.getPersonalNumber()));
        employee.setName(nvl(request.getName()));
        employee.setSurname(nvl(request.getSurname()));
        employee.setMiddleName(nvl(request.getMiddleName()));
        employee.setDuty(nvl(request.getDuty()));
        employee.setStructure(structure);

        return employee;
    }

    public void updateEntity(EmployeeEntity employee, EmployeeSaveRequest request, StructureEntity structure) {
        if (employee == null || request == null) return;

        employee.setPersonalCode(request.getPersonalNumber() != null ? request.getPersonalNumber() : employee.getPersonalCode());
        employee.setName(request.getName() != null ? request.getName() : employee.getName());
        employee.setSurname(request.getSurname() != null ? request.getSurname() : employee.getSurname());
        employee.setMiddleName(request.getMiddleName() != null ? request.getMiddleName() : employee.getMiddleName());
        employee.setDuty(request.getDuty() != null ? request.getDuty() : employee.getDuty());
        employee.setStructure(structure != null ? structure : employee.getStructure());
    }

    private List<FingerprintResponse> mapFingers(List<EmpFingersEntity> fingers) {
        if (fingers == null) return new ArrayList<>();

        List<FingerprintResponse> list = new ArrayList<>();
        for (EmpFingersEntity f : fingers) {
            FingerprintResponse fr = new FingerprintResponse();
            fr.setId(f.getId());
            fr.setFingerType(nvl(f.getFingerprint()));
            list.add(fr);
        }
        return list;
    }

    private String nvl(String s) {
        return s != null ? s : "";
    }
}
