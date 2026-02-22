package com.example.employee.controller;

import com.example.employee.model.dto.AuditFilterDto;
import com.example.employee.model.dto.PagedAuditResponseDto;
import com.example.employee.model.enums.AuditEntityTypeEnum;
import com.example.employee.model.enums.AuditRevTypeEnum;
import com.example.employee.service.concrete.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    /**
     * Bütün audit loqları — filterlər optional
     *
     * Nümunələr:
     *
     * Hamısı:
     * GET /api/v1/audit/logs
     *
     * Employee ID = 5-in bütün loqları:
     * GET /api/v1/audit/logs?employeeId=5
     *
     * Employee ID = 5-in yalnız foto və kart loqları:
     * GET /api/v1/audit/logs?employeeId=5&entityTypes=EMP_PHOTO,EMP_CARDS
     *
     * Yalnız silinmələr:
     * GET /api/v1/audit/logs?revisionTypes=DELETE
     *
     * Tarix aralığı:
     * GET /api/v1/audit/logs?dateFrom=2026-01-01T00:00:00Z&dateTo=2026-02-19T23:59:59Z
     *
     * Kim dəyişdirdi:
     * GET /api/v1/audit/logs?changedBy=admin_user
     *
     * Kart nömrəsi ilə:
     * GET /api/v1/audit/logs?cardNumber=4169123456789
     *
     * Hansı field dəyişib:
     * GET /api/v1/audit/logs?fieldName=phone
     *
     * Səhifələmə:
     * GET /api/v1/audit/logs?page=0&size=20
     */
    @GetMapping("/logs")
    public ResponseEntity<PagedAuditResponseDto> getAllLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        AuditFilterDto filter = AuditFilterDto.builder()
                .page(page)
                .size(size)
                .build();

        return ResponseEntity.ok(auditService.getFilteredLogs(filter));
    }
//    @GetMapping("/logs")
//    public ResponseEntity<PagedAuditResponseDto> getLogs(
//            @RequestParam(required = false)
//            Long employeeId,
//
//            @RequestParam(required = false)
//            List<AuditEntityTypeEnum> entityTypes,
//
//            @RequestParam(required = false)
//            List<AuditRevTypeEnum> revisionTypes,
//
//            @RequestParam(required = false)
//            String changedBy,
//
//            @RequestParam(required = false)
//            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
//            OffsetDateTime dateFrom,
//
//            @RequestParam(required = false)
//            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
//            OffsetDateTime dateTo,
//
//            @RequestParam(required = false)
//            String cardNumber,
//
//            @RequestParam(required = false)
//            String fieldName,
//
//            @RequestParam(defaultValue = "0")
//            int page,
//
//            @RequestParam(defaultValue = "20")
//            int size
//    ) {
//        AuditFilterDto filter = AuditFilterDto.builder()
//                .employeeId(employeeId)
//                .entityTypes(entityTypes)
//                .revisionTypes(revisionTypes)
//                .changedBy(changedBy)
//                .dateFrom(dateFrom)
//                .dateTo(dateTo)
//                .cardNumber(cardNumber)
//                .fieldName(fieldName)
//                .page(page)
//                .size(size)
//                .build();
//
//        return ResponseEntity.ok(auditService.getFilteredLogs(filter));
//    }

    /**
     * Konkret employee-nin loqları — shortcut
     *
     * GET /api/v1/audit/employee/5
     *
     * Əlavə filterlər də işləyir:
     * GET /api/v1/audit/employee/5?entityTypes=EMP_CARDS&revisionTypes=INSERT
     */
//
    @GetMapping("/search")
    public ResponseEntity<PagedAuditResponseDto> searchLogs(
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) List<AuditEntityTypeEnum> entityTypes,
            @RequestParam(required = false) List<AuditRevTypeEnum> revisionTypes,
            @RequestParam(required = false) String changedBy,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime dateFrom,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime dateTo,

            @RequestParam(required = false) String cardNumber,
            @RequestParam(required = false) String fieldName,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        AuditFilterDto filter = AuditFilterDto.builder()
                .employeeId(employeeId)     // optional
                .entityTypes(entityTypes)
                .revisionTypes(revisionTypes)
                .changedBy(changedBy)
                .dateFrom(dateFrom)
                .dateTo(dateTo)
                .cardNumber(cardNumber)
                .fieldName(fieldName)
                .page(page)
                .size(size)
                .build();

        return ResponseEntity.ok(auditService.getFilteredLogs(filter));
    }
}