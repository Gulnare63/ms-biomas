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



    boolean existsByEmployee_IdAndFingerIndexAndHand(Long employeeId, Integer fingerIndex, HandType hand);

    Optional<EmpFingersEntity> findByIdAndEmployee_Id(Long id, Long employeeId);

    List<EmpFingersEntity> findByEmployee_Id(Long employeeId);


    boolean existsByEmployeeIdAndFingerIndexAndHand(Long employeeId, Integer fingerIndex, HandType hand);

    long countByEmployeeId(Long employeeId);

    void deleteByEmployeeIdAndFingerIndexAndHand(Long employeeId, Integer fingerIndex, HandType hand);

    void deleteByEmployeeId(Long employeeId);
}
