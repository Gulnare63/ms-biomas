package com.example.employee.dao.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "emp_photo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpPhotoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String photo;   // filename
    private String folder;  // path

    @OneToOne
    @JoinColumn(name = "emp_id", nullable = false)
    private EmployeeEntity employee;
}
