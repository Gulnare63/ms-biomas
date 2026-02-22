package com.example.employee.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagedAuditResponseDto {

    private List<AuditLogDto> logs;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private AuditStatsDto stats;
}