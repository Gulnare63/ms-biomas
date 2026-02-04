package com.example.employee.dao.repository;

import com.example.employee.dao.entity.EmpFingersEntity;
import com.example.employee.model.enums.HandType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmpFingersRepository extends JpaRepository<EmpFingersEntity, Long> {

    List<EmpFingersEntity> findAllByEmployeeId(Long employeeId);

    Optional<EmpFingersEntity> findByEmployeeIdAndFingerIndexAndHand(
            Long employeeId,
            Integer fingerIndex,
            HandType hand
    );

}
