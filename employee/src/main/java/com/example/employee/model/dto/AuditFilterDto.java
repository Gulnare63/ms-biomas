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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditFilterDto {

    // Konkret employee-nin bütün loqları
    private Long employeeId;

    // Hansı entity tipləri — null gəlsə hamısı
    private List<AuditEntityTypeEnum> entityTypes;

    // Əməliyyat növü — null gəlsə hamısı
    private List<AuditRevTypeEnum> revisionTypes;

    // Kim dəyişdirdi
    private String changedBy;

    // Tarix aralığı
    private OffsetDateTime dateFrom;
    private OffsetDateTime dateTo;

    // Kart nömrəsi ilə axtarış
    private String cardNumber;

    // Hansı field dəyişib — "phone", "status" və s.
    private String fieldName;

    // Səhifələmə
    private int page = 0;
    private int size = 20;
}