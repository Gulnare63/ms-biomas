package com.example.employee.dao.repository;

import com.example.employee.dao.entity.EmpCardsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmpCardsRepository extends JpaRepository<EmpCardsEntity, Long> {

    boolean existsByNumber(String number);

    List<EmpCardsEntity> findByIsActive(Boolean isActive);

    Optional<EmpCardsEntity> findByIdAndIsActive(Long id, Boolean isActive);

    boolean existsByEmployee_IdAndIsActiveTrue(Long employeeId);
}