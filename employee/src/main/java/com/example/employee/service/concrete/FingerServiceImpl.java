package com.example.employee.service.concrete;

import com.example.employee.client.DeviceFingerClient;
import com.example.employee.dao.entity.EmpFingersEntity;
import com.example.employee.dao.entity.EmployeeEntity;
import com.example.employee.dao.repository.EmpFingersRepository;
import com.example.employee.dao.repository.EmployeeRepository;
import com.example.employee.exception.BadRequestException;
import com.example.employee.exception.NotFoundException;
import com.example.employee.model.enums.HandType;
import com.example.employee.model.request.FingerEnrollRequest;
import com.example.employee.model.request.FingerUpsertRequest;
import com.example.employee.model.response.FingerEnrollResponse;
import com.example.employee.model.request.RegisterFingerRequest;
import com.example.employee.model.response.FingerInfoResponse;
import com.example.employee.service.abstraction.FingerService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FingerServiceImpl implements FingerService {

    private final EmployeeRepository employeeRepository;
    private final EmpFingersRepository empFingersRepository;
    private final DeviceFingerClient deviceFingerClient;

@Override
@Transactional
public void registerFinger(RegisterFingerRequest request) {

    EmployeeEntity employee = employeeRepository.findById(request.getEmployeeId())
            .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

    if (request.getFingerIndex() == null || request.getFingerIndex() < 1 || request.getFingerIndex() > 5) {
        throw new IllegalArgumentException("fingerIndex must be between 1 and 5");
    }
    if (request.getEnrollDeviceId() == null) {
        throw new IllegalArgumentException("enrollDeviceId is required");
    }
    if (request.getHand() == null) {
        throw new IllegalArgumentException("hand is required");
    }

    FingerEnrollResponse resp = deviceFingerClient.enrollFinger(
            new FingerEnrollRequest(request.getEnrollDeviceId(), request.getFingerIndex())
    );

    if (resp == null || resp.getTemplateBase64() == null || resp.getTemplateBase64().isBlank()) {
        throw new RuntimeException("Device did not return fingerprint template");
    }

    if (resp.getFingerIndex() != null && !resp.getFingerIndex().equals(request.getFingerIndex())) {
        throw new RuntimeException("Finger index mismatch. Requested=" + request.getFingerIndex()
                + ", DeviceReturned=" + resp.getFingerIndex());
    }

    EmpFingersEntity finger = empFingersRepository
            .findByEmployeeIdAndFingerIndexAndHand(
                    employee.getId(),
                    request.getFingerIndex(),
                    request.getHand()
            )
            .orElseGet(() -> EmpFingersEntity.builder()
                    .employee(employee)
                    .fingerIndex(request.getFingerIndex())
                    .hand(request.getHand())
                    .build());

    finger.setFingerprint(resp.getTemplateBase64());
    empFingersRepository.save(finger);
}

    @Override
    public List<FingerInfoResponse> getFingers(Long employeeId) {
        employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        return empFingersRepository.findAllByEmployeeId(employeeId)
                .stream()
                .map(f -> FingerInfoResponse.builder()
                        .fingerIndex(f.getFingerIndex())
                        .hand(f.getHand())
                        .build())
                .toList();
    }


    @Override
    @Transactional
    public void upsertFinger(FingerUpsertRequest request) {
        EmployeeEntity employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new NotFoundException("Employee not found"));

        validateIndexHand(request.getFingerIndex(), request.getHand());

        if (request.getFingerprintBase64() == null || request.getFingerprintBase64().isBlank()) {
            throw new BadRequestException("fingerprintBase64 is required");
        }

        EmpFingersEntity finger = empFingersRepository
                .findByEmployeeIdAndFingerIndexAndHand(employee.getId(), request.getFingerIndex(), request.getHand())
                .orElseGet(() -> EmpFingersEntity.builder()
                        .employee(employee)
                        .fingerIndex(request.getFingerIndex())
                        .hand(request.getHand())
                        .build());

        finger.setFingerprint(request.getFingerprintBase64());
        empFingersRepository.save(finger);
    }

//    @Override
//    @Transactional(readOnly = true)
//    public List<FingerInfoResponse> getFingers(Long employeeId) {
//        employeeRepository.findById(employeeId)
//                .orElseThrow(() -> new NotFoundException("Employee not found"));
//
//        return empFingersRepository.findAllByEmployeeId(employeeId)
//                .stream()
//                .map(f -> FingerInfoResponse.builder()
//                        .id(f.getId())
//                        .fingerIndex(f.getFingerIndex())
//                        .hand(f.getHand())
//                        .hasTemplate(f.getFingerprint() != null && !f.getFingerprint().isBlank())
//                        .build())
//                .toList();
//    }

    @Override
    @Transactional(readOnly = true)
    public FingerInfoResponse getFinger(Long fingerId) {
        EmpFingersEntity f = empFingersRepository.findById(fingerId)
                .orElseThrow(() -> new NotFoundException("Finger not found"));

        return FingerInfoResponse.builder()
                .id(f.getId())
                .fingerIndex(f.getFingerIndex())
                .hand(f.getHand())
                .hasTemplate(f.getFingerprint() != null && !f.getFingerprint().isBlank())
                .build();
    }

    @Override
    @Transactional
    public void deleteFinger(Long fingerId) {
        EmpFingersEntity f = empFingersRepository.findById(fingerId)
                .orElseThrow(() -> new NotFoundException("Finger not found"));

        empFingersRepository.deleteById(f.getId());
    }

    @Override
    @Transactional
    public void deleteFinger(Long employeeId, Integer fingerIndex, String hand) {
        HandType ht;
        try {
            ht = HandType.valueOf(hand.toUpperCase());
        } catch (Exception e) {
            throw new BadRequestException("hand must be LEFT or RIGHT");
        }

        if (fingerIndex == null || fingerIndex < 1 || fingerIndex > 5) {
            throw new BadRequestException("fingerIndex must be between 1 and 5");
        }

        // employee var?
        employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("Employee not found"));

        // varsa sil, yoxdursa 404
        EmpFingersEntity f = empFingersRepository
                .findByEmployeeIdAndFingerIndexAndHand(employeeId, fingerIndex, ht)
                .orElseThrow(() -> new NotFoundException("Finger not found"));

        empFingersRepository.deleteById(f.getId());
    }

    private void validateIndexHandDevice(Integer fingerIndex, HandType hand, Long deviceId) {
        validateIndexHand(fingerIndex, hand);
        if (deviceId == null || deviceId <= 0) {
            throw new BadRequestException("enrollDeviceId is required");
        }
    }

    private void validateIndexHand(Integer fingerIndex, HandType hand) {
        if (fingerIndex == null || fingerIndex < 1 || fingerIndex > 5) {
            throw new BadRequestException("fingerIndex must be between 1 and 5");
        }
        if (hand == null) {
            throw new BadRequestException("hand is required");
        }
    }
}
