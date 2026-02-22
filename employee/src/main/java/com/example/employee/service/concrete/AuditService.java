package com.example.employee.service.concrete;

import com.example.employee.dao.entity.CustomRevisionEntity;
import com.example.employee.dao.entity.EmpCardsEntity;
import com.example.employee.dao.entity.EmpDetailsEntity;
import com.example.employee.dao.entity.EmpFingersEntity;
import com.example.employee.dao.entity.EmpPhotoEntity;
import com.example.employee.dao.entity.EmployeeEntity;
import com.example.employee.model.dto.AuditFilterDto;
import com.example.employee.model.dto.AuditLogDto;
import com.example.employee.model.dto.AuditStatsDto;
import com.example.employee.model.dto.FieldChangeDto;
import com.example.employee.model.dto.PagedAuditResponseDto;
import com.example.employee.model.enums.AuditEntityTypeEnum;
import com.example.employee.model.enums.AuditRevTypeEnum;
import jakarta.persistence.EntityManager;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuditService {

    private final EntityManager em;

    public AuditService(EntityManager em) {
        this.em = em;
    }

    // ================================================================
    // ANA METOD
    // ================================================================
    @Transactional(readOnly = true)
    public PagedAuditResponseDto getFilteredLogs(AuditFilterDto filter) {
        AuditReader reader = AuditReaderFactory.get(em);

        List<AuditEntityTypeEnum> targetTypes =
                (filter.getEntityTypes() != null
                        && !filter.getEntityTypes().isEmpty())
                        ? filter.getEntityTypes()
                        : List.of(AuditEntityTypeEnum.values());

        List<AuditLogDto> allLogs = new ArrayList<>();

        for (AuditEntityTypeEnum type : targetTypes) {
            switch (type) {
                case EMPLOYEE    -> allLogs.addAll(queryEmployee(reader, filter));
                case EMP_DETAILS -> allLogs.addAll(queryDetails(reader, filter));
                case EMP_PHOTO   -> allLogs.addAll(queryPhoto(reader, filter));
                case EMP_CARDS   -> allLogs.addAll(queryCards(reader, filter));
                case EMP_FINGERS -> allLogs.addAll(queryFingers(reader, filter));
            }
        }

        // Ən yeni əvvəl
        allLogs.sort(Comparator
                .comparing(AuditLogDto::getRevisionNumber)
                .reversed());

        // Field filter
        if (filter.getFieldName() != null
                && !filter.getFieldName().isBlank()) {
            allLogs = allLogs.stream()
                    .filter(log -> log.getChangedFields() != null
                            && log.getChangedFields().stream()
                            .anyMatch(f -> f.getFieldName()
                                    .equalsIgnoreCase(filter.getFieldName())))
                    .collect(Collectors.toList());
        }

        // Kart nömrəsi filter
        if (filter.getCardNumber() != null
                && !filter.getCardNumber().isBlank()) {
            allLogs = allLogs.stream()
                    .filter(log ->
                            log.getEntityType() == AuditEntityTypeEnum.EMP_CARDS
                            && log.getSnapshot() != null
                            && filter.getCardNumber().equals(
                                    log.getSnapshot().get("number")))
                    .collect(Collectors.toList());
        }

        AuditStatsDto stats = calcStats(allLogs);

        // Səhifələmə
        int total = allLogs.size();
        int from  = filter.getPage() * filter.getSize();
        int to    = Math.min(from + filter.getSize(), total);

        List<AuditLogDto> page = (from < total)
                ? allLogs.subList(from, to)
                : Collections.emptyList();

        return PagedAuditResponseDto.builder()
                .logs(page)
                .totalElements(total)
                .totalPages((int) Math.ceil((double) total / filter.getSize()))
                .currentPage(filter.getPage())
                .pageSize(filter.getSize())
                .stats(stats)
                .build();
    }

    // ================================================================
    // EMPLOYEE SORĞUSU
    // ================================================================
    private List<AuditLogDto> queryEmployee(
            AuditReader reader, AuditFilterDto filter) {

        AuditQuery query = reader.createQuery()
                .forRevisionsOfEntity(EmployeeEntity.class, false, true);

        if (filter.getEmployeeId() != null) {
            query.add(AuditEntity.id().eq(filter.getEmployeeId()));
        }

        applyCommonFilters(query, filter);

        List<Object[]> revisions = query.getResultList();

        Map<Long, EmployeeEntity> previousMap = new HashMap<>();
        List<AuditLogDto> result = new ArrayList<>();

        for (Object[] rev : revisions) {
            EmployeeEntity current        = (EmployeeEntity) rev[0];
            CustomRevisionEntity revInfo  = (CustomRevisionEntity) rev[1];
            RevisionType revType          = (RevisionType) rev[2];

            if (current == null) continue;

            EmployeeEntity previous = previousMap.get(current.getId());

            List<FieldChangeDto> changes  = diffEmployee(previous, current);
            Map<String, String>  snapshot = snapshotEmployee(current);

            result.add(AuditLogDto.builder()
                    .revisionNumber(revInfo.getId())
                    .revisionDate(toOffsetDateTime(revInfo.getTimestamp()))
                    .revisionType(mapRevType(revType))
                    .changedBy(revInfo.getChangedBy())
                    .entityType(AuditEntityTypeEnum.EMPLOYEE)
                    .entityId(current.getId())
                    .employeeId(current.getId())
                    .description(buildDescription(
                            AuditEntityTypeEnum.EMPLOYEE,
                            mapRevType(revType),
                            changes,
                            snapshot))
                    .changedFields(changes)
                    .snapshot(snapshot)
                    .build());

            previousMap.put(current.getId(), current);
        }

        return result;
    }

    // ================================================================
    // EMP_DETAILS SORĞUSU
    // ================================================================
    private List<AuditLogDto> queryDetails(
            AuditReader reader, AuditFilterDto filter) {

        AuditQuery query = reader.createQuery()
                .forRevisionsOfEntity(EmpDetailsEntity.class, false, true);

        if (filter.getEmployeeId() != null) {
            query.add(AuditEntity.relatedId("employee")
                    .eq(filter.getEmployeeId()));
        }

        applyCommonFilters(query, filter);

        List<Object[]> revisions = query.getResultList();

        Map<Long, EmpDetailsEntity> previousMap = new HashMap<>();
        List<AuditLogDto> result = new ArrayList<>();

        for (Object[] rev : revisions) {
            EmpDetailsEntity current      = (EmpDetailsEntity) rev[0];
            CustomRevisionEntity revInfo  = (CustomRevisionEntity) rev[1];
            RevisionType revType          = (RevisionType) rev[2];

            if (current == null) continue;

            EmpDetailsEntity previous = previousMap.get(current.getId());

            Long empId = current.getEmployee() != null
                    ? current.getEmployee().getId() : null;

            List<FieldChangeDto> changes  = diffDetails(previous, current);
            Map<String, String>  snapshot = snapshotDetails(current);

            result.add(AuditLogDto.builder()
                    .revisionNumber(revInfo.getId())
                    .revisionDate(toOffsetDateTime(revInfo.getTimestamp()))
                    .revisionType(mapRevType(revType))
                    .changedBy(revInfo.getChangedBy())
                    .entityType(AuditEntityTypeEnum.EMP_DETAILS)
                    .entityId(current.getId())
                    .employeeId(empId)
                    .description(buildDescription(
                            AuditEntityTypeEnum.EMP_DETAILS,
                            mapRevType(revType),
                            changes,
                            snapshot))
                    .changedFields(changes)
                    .snapshot(snapshot)
                    .build());

            previousMap.put(current.getId(), current);
        }

        return result;
    }

    // ================================================================
    // EMP_PHOTO SORĞUSU
    // ================================================================
    private List<AuditLogDto> queryPhoto(
            AuditReader reader, AuditFilterDto filter) {

        AuditQuery query = reader.createQuery()
                .forRevisionsOfEntity(EmpPhotoEntity.class, false, true);

        if (filter.getEmployeeId() != null) {
            query.add(AuditEntity.relatedId("employee")
                    .eq(filter.getEmployeeId()));
        }

        applyCommonFilters(query, filter);

        List<Object[]> revisions = query.getResultList();
        List<AuditLogDto> result = new ArrayList<>();

        for (Object[] rev : revisions) {
            EmpPhotoEntity current        = (EmpPhotoEntity) rev[0];
            CustomRevisionEntity revInfo  = (CustomRevisionEntity) rev[1];
            RevisionType revType          = (RevisionType) rev[2];

            if (current == null) continue;

            Long empId = current.getEmployee() != null
                    ? current.getEmployee().getId() : null;

            Map<String, String> snapshot = snapshotPhoto(current);

            result.add(AuditLogDto.builder()
                    .revisionNumber(revInfo.getId())
                    .revisionDate(toOffsetDateTime(revInfo.getTimestamp()))
                    .revisionType(mapRevType(revType))
                    .changedBy(revInfo.getChangedBy())
                    .entityType(AuditEntityTypeEnum.EMP_PHOTO)
                    .entityId(current.getId())
                    .employeeId(empId)
                    .description(buildDescription(
                            AuditEntityTypeEnum.EMP_PHOTO,
                            mapRevType(revType),
                            Collections.emptyList(),
                            snapshot))
                    .changedFields(Collections.emptyList())
                    .snapshot(snapshot)
                    .build());
        }

        return result;
    }

    // ================================================================
    // EMP_CARDS SORĞUSU
    // ================================================================
    private List<AuditLogDto> queryCards(
            AuditReader reader, AuditFilterDto filter) {

        AuditQuery query = reader.createQuery()
                .forRevisionsOfEntity(EmpCardsEntity.class, false, true);

        if (filter.getEmployeeId() != null) {
            query.add(AuditEntity.relatedId("employee")
                    .eq(filter.getEmployeeId()));
        }

        applyCommonFilters(query, filter);

        List<Object[]> revisions = query.getResultList();

        Map<Long, EmpCardsEntity> previousMap = new HashMap<>();
        List<AuditLogDto> result = new ArrayList<>();

        for (Object[] rev : revisions) {
            EmpCardsEntity current        = (EmpCardsEntity) rev[0];
            CustomRevisionEntity revInfo  = (CustomRevisionEntity) rev[1];
            RevisionType revType          = (RevisionType) rev[2];

            if (current == null) continue;

            EmpCardsEntity previous = previousMap.get(current.getId());

            Long empId = current.getEmployee() != null
                    ? current.getEmployee().getId() : null;

            List<FieldChangeDto> changes  = diffCard(previous, current);
            Map<String, String>  snapshot = snapshotCard(current);

            result.add(AuditLogDto.builder()
                    .revisionNumber(revInfo.getId())
                    .revisionDate(toOffsetDateTime(revInfo.getTimestamp()))
                    .revisionType(mapRevType(revType))
                    .changedBy(revInfo.getChangedBy())
                    .entityType(AuditEntityTypeEnum.EMP_CARDS)
                    .entityId(current.getId())
                    .employeeId(empId)
                    .description(buildDescription(
                            AuditEntityTypeEnum.EMP_CARDS,
                            mapRevType(revType),
                            changes,
                            snapshot))
                    .changedFields(changes)
                    .snapshot(snapshot)
                    .build());

            previousMap.put(current.getId(), current);
        }

        return result;
    }

    // ================================================================
    // EMP_FINGERS SORĞUSU
    // ================================================================
    private List<AuditLogDto> queryFingers(
            AuditReader reader, AuditFilterDto filter) {

        AuditQuery query = reader.createQuery()
                .forRevisionsOfEntity(EmpFingersEntity.class, false, true);

        if (filter.getEmployeeId() != null) {
            query.add(AuditEntity.relatedId("employee")
                    .eq(filter.getEmployeeId()));
        }

        applyCommonFilters(query, filter);

        List<Object[]> revisions = query.getResultList();
        List<AuditLogDto> result = new ArrayList<>();

        for (Object[] rev : revisions) {
            EmpFingersEntity current      = (EmpFingersEntity) rev[0];
            CustomRevisionEntity revInfo  = (CustomRevisionEntity) rev[1];
            RevisionType revType          = (RevisionType) rev[2];

            if (current == null) continue;

            Long empId = current.getEmployee() != null
                    ? current.getEmployee().getId() : null;

            Map<String, String> snapshot = snapshotFingers(current);

            result.add(AuditLogDto.builder()
                    .revisionNumber(revInfo.getId())
                    .revisionDate(toOffsetDateTime(revInfo.getTimestamp()))
                    .revisionType(mapRevType(revType))
                    .changedBy(revInfo.getChangedBy())
                    .entityType(AuditEntityTypeEnum.EMP_FINGERS)
                    .entityId(current.getId())
                    .employeeId(empId)
                    .description(buildDescription(
                            AuditEntityTypeEnum.EMP_FINGERS,
                            mapRevType(revType),
                            Collections.emptyList(),
                            snapshot))
                    .changedFields(Collections.emptyList())
                    .snapshot(snapshot)
                    .build());
        }

        return result;
    }

    // ================================================================
    // ÜMUMI FILTER — bütün sorğulara tətbiq olunur
    // ================================================================
    private void applyCommonFilters(AuditQuery query, AuditFilterDto filter) {

        if (filter.getDateFrom() != null) {
            query.add(AuditEntity.revisionProperty("timestamp")
                    .ge(filter.getDateFrom().toInstant().toEpochMilli()));
        }

        if (filter.getDateTo() != null) {
            query.add(AuditEntity.revisionProperty("timestamp")
                    .le(filter.getDateTo().toInstant().toEpochMilli()));
        }

        if (filter.getChangedBy() != null
                && !filter.getChangedBy().isBlank()) {
            query.add(AuditEntity.revisionProperty("changedBy")
                    .eq(filter.getChangedBy()));
        }

        if (filter.getRevisionTypes() != null
                && !filter.getRevisionTypes().isEmpty()) {

            List<RevisionType> types = filter.getRevisionTypes().stream()
                    .map(this::toHibernateRevType)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (types.size() == 1) {
                query.add(AuditEntity.revisionType().eq(types.get(0)));
            } else if (types.size() > 1) {
                query.add(AuditEntity.revisionType().in(types));
            }
        }

        // Diff üçün əvvəldən sonraya sırala
        query.addOrder(AuditEntity.revisionNumber().asc());
    }

    // ================================================================
    // DIFF METODLARI
    // ================================================================
    private List<FieldChangeDto> diffEmployee(
            EmployeeEntity old, EmployeeEntity current) {

        List<FieldChangeDto> changes = new ArrayList<>();
        if (old == null) return changes;

        checkChange(changes, "name",
                old.getName(), current.getName());
        checkChange(changes, "surname",
                old.getSurname(), current.getSurname());
        checkChange(changes, "middleName",
                old.getMiddleName(), current.getMiddleName());
        checkChange(changes, "personalCode",
                old.getPersonalCode(), current.getPersonalCode());
        checkChange(changes, "isActive",
                str(old.getIsActive()), str(current.getIsActive()));
        checkChange(changes, "status",
                str(old.getStatus()), str(current.getStatus()));
        checkChange(changes, "duty",
                old.getDuty() != null ? old.getDuty().getName() : null,
                current.getDuty() != null ? current.getDuty().getName() : null);
        checkChange(changes, "workShift",
                old.getWorkShift() != null ? old.getWorkShift().getName() : null,
                current.getWorkShift() != null ? current.getWorkShift().getName() : null);
        checkChange(changes, "structure",
                old.getStructure() != null ? old.getStructure().getName() : null,
                current.getStructure() != null ? current.getStructure().getName() : null);

        return changes;
    }

    private List<FieldChangeDto> diffDetails(
            EmpDetailsEntity old, EmpDetailsEntity current) {

        List<FieldChangeDto> changes = new ArrayList<>();
        if (old == null) return changes;

        checkChange(changes, "phone",     old.getPhone(),    current.getPhone());
        checkChange(changes, "email",     old.getEmail(),    current.getEmail());
        checkChange(changes, "gender",    old.getGender(),   current.getGender());
        checkChange(changes, "address",   old.getAddress(),  current.getAddress());
        checkChange(changes, "birthDate",
                str(old.getBirthDate()), str(current.getBirthDate()));

        return changes;
    }

    private List<FieldChangeDto> diffCard(
            EmpCardsEntity old, EmpCardsEntity current) {

        List<FieldChangeDto> changes = new ArrayList<>();
        if (old == null) return changes;

        checkChange(changes, "name",
                old.getName(),     current.getName());
        checkChange(changes, "number",
                old.getNumber(),   current.getNumber());
        checkChange(changes, "isActive",
                str(old.getIsActive()), str(current.getIsActive()));

        return changes;
    }

    // ================================================================
    // SNAPSHOT METODLARI
    // ================================================================
    private Map<String, String> snapshotEmployee(EmployeeEntity e) {
        if (e == null) return Map.of();
        Map<String, String> m = new LinkedHashMap<>();
        m.put("name",         e.getName());
        m.put("surname",      e.getSurname());
        m.put("middleName",   e.getMiddleName());
        m.put("personalCode", e.getPersonalCode());
        m.put("isActive",     str(e.getIsActive()));
        m.put("status",       str(e.getStatus()));
        m.put("duty",         e.getDuty() != null
                ? e.getDuty().getName() : null);
        m.put("workShift",    e.getWorkShift() != null
                ? e.getWorkShift().getName() : null);
        m.put("structure",    e.getStructure() != null
                ? e.getStructure().getName() : null);
        return m;
    }

    private Map<String, String> snapshotDetails(EmpDetailsEntity d) {
        if (d == null) return Map.of();
        Map<String, String> m = new LinkedHashMap<>();
        m.put("phone",     d.getPhone());
        m.put("email",     d.getEmail());
        m.put("gender",    d.getGender());
        m.put("address",   d.getAddress());
        m.put("birthDate", str(d.getBirthDate()));
        return m;
    }

    private Map<String, String> snapshotPhoto(EmpPhotoEntity p) {
        if (p == null) return Map.of();
        Map<String, String> m = new LinkedHashMap<>();
        m.put("objectName",  p.getObjectName());
        m.put("contentType", p.getContentType());
        m.put("sizeBytes",   str(p.getSizeBytes()));
        m.put("status",      str(p.getStatus()));
        m.put("createdAt",   str(p.getCreatedAt()));
        return m;
    }

    private Map<String, String> snapshotCard(EmpCardsEntity c) {
        if (c == null) return Map.of();
        Map<String, String> m = new LinkedHashMap<>();
        m.put("name",     c.getName());
        m.put("number",   c.getNumber());
        m.put("isActive", str(c.getIsActive()));
        return m;
    }

    private Map<String, String> snapshotFingers(EmpFingersEntity f) {
        if (f == null) return Map.of();
        Map<String, String> m = new LinkedHashMap<>();
        m.put("fingerIndex",    str(f.getFingerIndex()));
        m.put("hand",           str(f.getHand()));
        // fingerprint saxlamırıq — çox böyük ola bilər
        m.put("hasFingerprint", f.getFingerprint() != null ? "true" : "false");
        return m;
    }

    // ================================================================
    // DESCRIPTION BUILDER — changedFields-dən avtomatik oxuyur
    // heç vaxt boş olmaz
    // ================================================================
    private String buildDescription(
            AuditEntityTypeEnum entityType,
            AuditRevTypeEnum revType,
            List<FieldChangeDto> changedFields,
            Map<String, String> snapshot) {

        String entityLabel = switch (entityType) {
            case EMPLOYEE    -> "Employee";
            case EMP_DETAILS -> "Əlaqə məlumatları";
            case EMP_PHOTO   -> "Profil şəkli";
            case EMP_CARDS   -> "Kart";
            case EMP_FINGERS -> "Barmaq izi";
        };

        String prefix = switch (revType) {
            case INSERT -> entityLabel + " əlavə edildi";
            case DELETE -> entityLabel + " silindi";
            case UPDATE -> entityLabel + " dəyişdi";
        };

        // INSERT və DELETE — snapshot-dan əlavə məlumat
        if (revType == AuditRevTypeEnum.INSERT
                || revType == AuditRevTypeEnum.DELETE) {
            String extra = buildExtraFromSnapshot(entityType, snapshot);
            return extra.isBlank() ? prefix : prefix + ": " + extra;
        }

        // UPDATE — changedFields-dən oxu
        if (changedFields == null || changedFields.isEmpty()) {
            return prefix;
        }

        List<String> parts = changedFields.stream()
                .map(this::buildFieldDescription)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());

        return parts.isEmpty()
                ? prefix
                : prefix + ": " + String.join(", ", parts);
    }

    private String buildFieldDescription(FieldChangeDto field) {

        String label  = fieldLabel(field.getFieldName());
        String oldVal = field.getOldValue();
        String newVal = field.getNewValue();

        // Boolean fieldlər
        if ("isActive".equals(field.getFieldName())) {
            return "true".equals(newVal)
                    ? "aktivləşdirildi"
                    : "deaktiv edildi";
        }

        if (oldVal == null && newVal != null)
            return label + " təyin edildi: '" + newVal + "'";

        if (oldVal != null && newVal == null)
            return label + " silindi";

        return label + " '"
                + shorten(oldVal, 30)
                + "' → '"
                + shorten(newVal, 30) + "'";
    }

    private String buildExtraFromSnapshot(
            AuditEntityTypeEnum entityType,
            Map<String, String> snapshot) {

        if (snapshot == null) return "";

        return switch (entityType) {
            case EMPLOYEE -> {
                String name    = snapshot.getOrDefault("name", "");
                String surname = snapshot.getOrDefault("surname", "");
                yield (name + " " + surname).trim();
            }
            case EMP_DETAILS -> {
                List<String> parts = new ArrayList<>();
                String phone = snapshot.get("phone");
                String email = snapshot.get("email");
                if (phone != null) parts.add("tel: " + phone);
                if (email != null) parts.add("email: " + email);
                yield String.join(", ", parts);
            }
            case EMP_PHOTO -> {
                String obj = snapshot.get("objectName");
                yield obj != null ? obj : "";
            }
            case EMP_CARDS -> {
                String name   = snapshot.getOrDefault("name", "");
                String number = snapshot.getOrDefault("number", "");
                yield ("'" + name + "' №" + number).trim();
            }
            case EMP_FINGERS -> {
                String hand  = snapshot.getOrDefault("hand", "");
                String index = snapshot.getOrDefault("fingerIndex", "");
                yield hand + " əl, " + index + "-ci barmaq";
            }
        };
    }

    private String fieldLabel(String fieldName) {
        return switch (fieldName) {
            case "name"         -> "Ad";
            case "surname"      -> "Soyad";
            case "middleName"   -> "Ata adı";
            case "personalCode" -> "Şəxsi kod";
            case "isActive"     -> "Aktivlik";
            case "status"       -> "Status";
            case "duty"         -> "Vəzifə";
            case "workShift"    -> "İş növbəsi";
            case "structure"    -> "Struktur";
            case "phone"        -> "Telefon";
            case "email"        -> "Email";
            case "gender"       -> "Cins";
            case "address"      -> "Ünvan";
            case "birthDate"    -> "Doğum tarixi";
            case "number"       -> "Kart nömrəsi";
            case "fingerIndex"  -> "Barmaq";
            case "hand"         -> "Əl";
            case "objectName"   -> "Fayl adı";
            case "contentType"  -> "Fayl tipi";
            case "sizeBytes"    -> "Fayl ölçüsü";
            default             -> fieldName;
        };
    }

    // ================================================================
    // STATİSTİKA
    // ================================================================
    private AuditStatsDto calcStats(List<AuditLogDto> logs) {

        long todayStart = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        long today = logs.stream()
                .filter(l -> l.getRevisionDate() != null
                        && l.getRevisionDate()
                        .toInstant().toEpochMilli() >= todayStart)
                .count();

        long inserts = logs.stream()
                .filter(l -> l.getRevisionType() == AuditRevTypeEnum.INSERT)
                .count();
        long updates = logs.stream()
                .filter(l -> l.getRevisionType() == AuditRevTypeEnum.UPDATE)
                .count();
        long deletes = logs.stream()
                .filter(l -> l.getRevisionType() == AuditRevTypeEnum.DELETE)
                .count();

        String mostActiveUser = logs.stream()
                .filter(l -> l.getChangedBy() != null)
                .collect(Collectors.groupingBy(
                        AuditLogDto::getChangedBy,
                        Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        String mostChangedEntity = logs.stream()
                .filter(l -> l.getEntityType() != null)
                .collect(Collectors.groupingBy(
                        l -> l.getEntityType().name(),
                        Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        return AuditStatsDto.builder()
                .totalChangesToday(today)
                .totalInserts(inserts)
                .totalUpdates(updates)
                .totalDeletes(deletes)
                .mostActiveUser(mostActiveUser)
                .mostChangedEntityType(mostChangedEntity)
                .build();
    }

    // ================================================================
    // KÖMƏKÇİ METODLAR
    // ================================================================
    private void checkChange(List<FieldChangeDto> list,
                             String field,
                             String oldVal,
                             String newVal) {
        if (!Objects.equals(oldVal, newVal)) {
            list.add(FieldChangeDto.builder()
                    .fieldName(field)
                    .oldValue(oldVal)
                    .newValue(newVal)
                    .build());
        }
    }

    private AuditRevTypeEnum mapRevType(RevisionType type) {
        return switch (type) {
            case ADD -> AuditRevTypeEnum.INSERT;
            case MOD -> AuditRevTypeEnum.UPDATE;
            case DEL -> AuditRevTypeEnum.DELETE;
        };
    }

    private RevisionType toHibernateRevType(AuditRevTypeEnum type) {
        return switch (type) {
            case INSERT -> RevisionType.ADD;
            case UPDATE -> RevisionType.MOD;
            case DELETE -> RevisionType.DEL;
        };
    }

    private OffsetDateTime toOffsetDateTime(long epochMilli) {
        return Instant.ofEpochMilli(epochMilli)
                .atOffset(ZoneOffset.UTC);
    }

    private String str(Object o) {
        return o != null ? o.toString() : null;
    }

    private String shorten(String val, int maxLen) {
        if (val == null) return "";
        return val.length() > maxLen
                ? val.substring(0, maxLen) + "..."
                : val;
    }
}