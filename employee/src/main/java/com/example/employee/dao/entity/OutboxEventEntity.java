package com.example.employee.dao.entity;

import com.example.employee.model.enums.OutboxStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox_event",
        indexes = {
                @Index(name = "idx_outbox_status_next_retry", columnList = "status,next_retry_at")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OutboxEventEntity {

    @Id
    private UUID id;

    @Column( nullable = false, length = 60)
    private String aggregateType; // EMPLOYEE

    @Column( nullable = false)
    private String aggregateId; // employeeId as String

    @Column(nullable = false, length = 80)
    private String eventType; // EMPLOYEE_CREATED

    @Lob
    @Column(nullable = false)
    private String payload; // JSON

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OutboxStatus status;

    @Column( nullable = false)
    private int attemptCount;

    private LocalDateTime nextRetryAt;

    @Column( length = 500)
    private String lastError;

    @Column( nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}