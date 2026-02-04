package com.example.employee.dao.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "work_shift")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class WorkShiftEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;          // seher smenimi  axsammi onu gosterir
    private LocalTime startTime;
    private LocalTime endTime;

    @OneToMany(mappedBy = "workShift")
    private List<EmployeeEntity> employees;
}



