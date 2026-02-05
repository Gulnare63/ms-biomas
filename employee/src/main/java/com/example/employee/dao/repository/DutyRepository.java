package com.example.employee.dao.repository;

import com.example.employee.dao.entity.DutyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DutyRepository extends JpaRepository<DutyEntity, Long> {
}
