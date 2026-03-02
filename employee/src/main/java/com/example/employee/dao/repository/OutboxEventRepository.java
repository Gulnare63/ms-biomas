package com.example.employee.dao.repository;

import com.example.employee.dao.entity.OutboxEventEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEventEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
           select e from OutboxEventEntity e
           where e.status = com.example.employee.model.enums.OutboxStatus.PENDING
             and (e.nextRetryAt is null or e.nextRetryAt <= :now)
           order by e.createdAt asc
           """)
    List<OutboxEventEntity> findPendingForUpdate(@Param("now") LocalDateTime now, Pageable pageable);
}