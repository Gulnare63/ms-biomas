package com.example.audit.dao.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_ts", columnList = "timestamp"),
        @Index(name = "idx_audit_user", columnList = "username"),
        @Index(name = "idx_audit_module", columnList = "module")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AuditLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String username;

    @Column(nullable = false, length = 10)
    private String action;

    @Column(nullable = false, length = 300)
    private String path;

    @Column(nullable = false, length = 50)
    private String module;

    @Column(length = 200)
    private String handler;

    @Column(columnDefinition = "text")
    private String arguments;


    private Integer status;
    private Long durationMs;

    @Column(columnDefinition = "text")
    private String errorMessage;

    @Column(length = 60)
    private String ip;

    @CreationTimestamp
    private LocalDateTime timestamp;
}


