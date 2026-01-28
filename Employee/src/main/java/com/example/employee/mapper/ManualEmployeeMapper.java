package com.example.employee.mapper;

import com.example.employee.dao.entity.EmployeeEntity;
import com.example.employee.dao.entity.StructureEntity;
import com.example.employee.dao.entity.EmpCardsEntity;
import com.example.employee.dao.entity.EmpFingersEntity;
import com.example.employee.dao.entity.EmpPhotoEntity;
import com.example.employee.model.request.EmployeeSaveRequest;
import com.example.employee.model.response.EmployeeDetailResponse;
import com.example.employee.model.response.EmployeeListResponse;
import com.example.employee.model.response.FingerprintResponse;

import java.util.ArrayList;
import java.util.List;

public class ManualEmployeeMapper {
    public EmployeeListResponse toListResponse(EmployeeEntity employee) {
        if (employee == null) return null;

        EmployeeListResponse response = new EmployeeListResponse();
        response.setUserId(employee.getId());
        response.setPersonalNumber(employee.getPersonalCode() != null ? employee.getPersonalCode() : "");
        response.setName(employee.getName() != null ? employee.getName() : "");
        response.setSurname(employee.getSurname() != null ? employee.getSurname() : "");
        response.setMiddleName(employee.getMiddleName() != null ? employee.getMiddleName() : "");
        response.setDuty(employee.getDuty() != null ? employee.getDuty() : "");
        response.setStructureName(employee.getStructure() != null ? employee.getStructure().getName() : "");
        response.setHasCard(employee.getCards() != null && !employee.getCards().isEmpty());
        response.setHasFace(employee.getPhoto() != null);
        response.setHasFinger(employee.getFingers() != null && !employee.getFingers().isEmpty());

        return response;
    }

    public EmployeeDetailResponse toDetailResponse(EmployeeEntity employee) {
        if (employee == null) return null;

        EmployeeDetailResponse response = new EmployeeDetailResponse();
        response.setUserId(employee.getId());
        response.setPersonalNumber(employee.getPersonalCode() != null ? employee.getPersonalCode() : "");
        response.setName(employee.getName() != null ? employee.getName() : "");
        response.setSurname(employee.getSurname() != null ? employee.getSurname() : "");
        response.setMiddleName(employee.getMiddleName() != null ? employee.getMiddleName() : "");
        response.setDuty(employee.getDuty() != null ? employee.getDuty() : "");
        response.setStructureName(employee.getStructure() != null ? employee.getStructure().getName() : "");

        EmpPhotoEntity photo = employee.getPhoto();
        response.setFace(photo != null ? photo.getPhoto() : null);

        response.setCard(employee.getCards() != null && !employee.getCards().isEmpty() ? employee.getCards().get(0) : null);

        response.setEmployeeFingers(mapFingers(employee.getFingers()));

        response.setEmployeeFingerDataList(new ArrayList<>());

        return response;
    }

    public EmployeeEntity toEntity(EmployeeSaveRequest request, StructureEntity structure) {
        if (request == null) return null;

        EmployeeEntity employee = new EmployeeEntity();
        employee.setPersonalCode(request.getPersonalNumber() != null ? request.getPersonalNumber() : "");
        employee.setName(request.getName() != null ? request.getName() : "");
        employee.setSurname(request.getSurname() != null ? request.getSurname() : "");
        employee.setMiddleName(request.getMiddleName() != null ? request.getMiddleName() : "");
        employee.setDuty(request.getDuty() != null ? request.getDuty() : "");
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
            fr.setFingerType(f.getFingerprint() != null ? f.getFingerprint() : "");
            list.add(fr);
        }
        return list;
    }
}
