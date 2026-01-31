package com.example.attendance.dao.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "total_work_hour")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TotalWorkHourEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long employeeId;

    private LocalDate workDate;

    private LocalDateTime workStartDate;
    private LocalDateTime workEndDate;

    private LocalDateTime firstIn;
    private LocalDateTime lastOut;

    private Long totalWorkMinutes;
}

