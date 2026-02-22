package com.example.employee.dao.entity;

import com.example.employee.model.enums.HandType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

@Entity
@Table(
        name = "emp_fingers",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_emp_fingers_emp_idx_hand",
                columnNames = {"emp_id", "finger_index", "hand"}
        )
)
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpFingersEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String fingerprint;

    @Column(name = "finger_index", nullable = false)
    private Integer fingerIndex;  // 1–5

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private HandType hand;

    @ManyToOne(fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @JoinColumn(name = "emp_id", nullable = false)
    private EmployeeEntity employee;
}
