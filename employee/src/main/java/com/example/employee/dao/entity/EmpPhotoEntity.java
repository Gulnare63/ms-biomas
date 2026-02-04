package com.example.employee.dao.entity;

import com.example.employee.model.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "emp_photo", indexes = {
        @Index(name = "idx_emp_photo_employee_id", columnList = "emp_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpPhotoEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "emp_id", nullable = false)
    private EmployeeEntity employee;

    // storage-də folder: employees/{id}/photo
    @Column(name="folder", nullable=false, length=300)
    private String folder;

    // storage-də objectName: uuid.png/jpg
    @Column(name="object_name" ,nullable=false, length=200)
    private String objectName;

    @Column(length=100)
    private String contentType;

    private Long sizeBytes;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private Status status;

    @Column(nullable=false)
    private OffsetDateTime createdAt;

}
