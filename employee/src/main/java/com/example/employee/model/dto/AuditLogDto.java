package com.example.employee.model.dto;

import com.example.employee.model.enums.AuditEntityTypeEnum;
import com.example.employee.model.enums.AuditRevTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogDto {

    // Revision məlumatları
    private Integer revisionNumber;
    private OffsetDateTime revisionDate;
    private AuditRevTypeEnum revisionType;
    private String changedBy;

    // Hansı entity dəyişib
    private AuditEntityTypeEnum entityType;
    private Long entityId;

    // Bütün entity-lər üçün employee id
    // EmpCards loqu gələndə belə employeeId burada olur
    private Long employeeId;

    // Admin ekranında oxunaqlı mətn
    private String description;

    // Dəyişən fieldlər — UPDATE zamanı dolu olur
    private List<FieldChangeDto> changedFields;

    // O andakı tam vəziyyət — detail modal üçün
    private Map<String, String> snapshot;
}