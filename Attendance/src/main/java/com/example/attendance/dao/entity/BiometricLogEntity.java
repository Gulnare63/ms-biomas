package com.example.attendance.dao.entity;

import com.example.attendance.model.enums.InOutType;
import com.example.attendance.model.enums.VerifyType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "biometric_log")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class BiometricLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long employeeId;

    private String fullName;
    private String cardNo;

    private LocalDateTime dateTime;

    @Enumerated(EnumType.STRING)
    private VerifyType verify;

    @Enumerated(EnumType.STRING)
    private InOutType inOut;

    private String deviceId;

    private LocalDateTime createTimestamp;
}
