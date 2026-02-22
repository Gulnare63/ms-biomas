package com.example.employee.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditStatsDto {

    private long totalChangesToday;
    private long totalInserts;
    private long totalUpdates;
    private long totalDeletes;
    private String mostActiveUser;
    private String mostChangedEntityType;
}