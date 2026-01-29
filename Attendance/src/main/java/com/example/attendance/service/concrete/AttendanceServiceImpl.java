package com.example.attendance.service.concrete;

import com.example.attendance.client.EmployeeClient;
import com.example.attendance.client.WorkShiftDto;
import com.example.attendance.dao.entity.BiometricLogEntity;
import com.example.attendance.dao.entity.TotalWorkHourEntity;
import com.example.attendance.dao.repository.BiometricLogRepository;
import com.example.attendance.dao.repository.TotalWorkHourRepository;
import com.example.attendance.model.enums.InOutType;
import com.example.attendance.model.request.AttendanceStatusDto;
import com.example.attendance.service.abstraction.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final BiometricLogRepository biometricLogRepository;
    private final TotalWorkHourRepository totalWorkHourRepository;
    private final EmployeeClient employeeClient;

    @Override
    public TotalWorkHourEntity calculateTotalWork(Long employeeId, LocalDate date) {

        // Gün aralığı
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEndExclusive = date.plusDays(1).atStartOfDay(); // [dayStart, nextDayStart)

        // 1) Logları çəkdim və sort etdim
        List<BiometricLogEntity> logs = biometricLogRepository
                .findByEmployeeIdAndDateTimeBetween(employeeId, dayStart, dayEndExclusive.minusNanos(1));

        if (logs.isEmpty()) {
            throw new RuntimeException("No biometric logs for this date");
        }

        logs.sort(Comparator.comparing(BiometricLogEntity::getDateTime));

        // 2) Shift-i götür  modullara bolduyum ucun feign client ile etdim
        WorkShiftDto shift = employeeClient.getEmployeeShift(employeeId);
        if (shift == null || shift.getStartTime() == null || shift.getEndTime() == null) {
            throw new RuntimeException("Employee shift not found or invalid");
        }

        LocalDateTime shiftStart = LocalDateTime.of(date, shift.getStartTime());
        LocalDateTime shiftEnd = LocalDateTime.of(date, shift.getEndTime());

        //  gece smeni olsa  endi  bir  gun artiqmaq ? mellimden sorus bunu
        if (!shiftEnd.isAfter(shiftStart)) {
            shiftEnd = shiftEnd.plusDays(1);
        }

        // 3) First IN / Last OUT
        LocalDateTime firstIn = logs.stream()
                .filter(l -> l.getInOut() == InOutType.IN)
                .map(BiometricLogEntity::getDateTime)
                .min(Comparator.naturalOrder())
                .orElse(null);

        LocalDateTime lastOut = logs.stream()
                .filter(l -> l.getInOut() == InOutType.OUT)
                .map(BiometricLogEntity::getDateTime)
                .max(Comparator.naturalOrder())
                .orElse(null);

        if (firstIn == null) {
            throw new RuntimeException("No IN log found for this date");
        }

        // 4) Total work: IN/OUT cütləmə ilə hesablamaq  butun variantlari mellimle dusunmeliyem
        long totalMinutes = calculateWorkedMinutesByPairs(logs, shiftEnd);

        // 5) Upsert: həmin gün üçün əvvəl yazılıbsa update et
        TotalWorkHourEntity entity = totalWorkHourRepository
                .findByEmployeeIdAndWorkDate(employeeId, date)
                .orElseGet(TotalWorkHourEntity::new);

        entity.setEmployeeId(employeeId);
        entity.setWorkDate(date);
        entity.setWorkStartDate(shiftStart);
        entity.setWorkEndDate(shiftEnd);
        entity.setFirstIn(firstIn);
        entity.setLastOut(lastOut); // null ola bilər (əgər OUT yoxdursa)
        entity.setTotalWorkMinutes(totalMinutes);

        return totalWorkHourRepository.save(entity);
    }

    /**
     * IN/OUT cütləmə:
     * - IN görsə, "openIn" açır
     * - OUT görsə və openIn varsa, intervalı toplayır
     * - Əgər günün sonunda openIn qalarsa (OUT yoxdursa), end olaraq shiftEnd istifadə edir
     * <p>
     * Qeyd: Bu variant 8-də gəlibsə, 9-a qədər olan vaxtı DA sayır (overtime/early arrival).
     */
    private long calculateWorkedMinutesByPairs(List<BiometricLogEntity> logs, LocalDateTime fallbackEnd) {

        LocalDateTime openIn = null;
        long total = 0;

        for (BiometricLogEntity log : logs) {
            LocalDateTime t = log.getDateTime();

            if (log.getInOut() == InOutType.IN) {
                // ard-arda IN gəlirsə, ən birincisini saxlayırıq
                if (openIn == null) {
                    openIn = t;
                }
            } else if (log.getInOut() == InOutType.OUT) {
                // OUT gəlib, amma əvvəl IN yoxdursa, ignore
                if (openIn != null) {
                    if (t.isAfter(openIn)) {
                        total += Duration.between(openIn, t).toMinutes();
                    }
                    openIn = null; // session bağlandı
                }
            }
        }

        // Gün OUT ilə bağlanmayıbsa (axırda IN qalıbsa)
        if (openIn != null) {
            LocalDateTime end = (fallbackEnd != null && fallbackEnd.isAfter(openIn)) ? fallbackEnd : openIn;
            total += Duration.between(openIn, end).toMinutes();
        }

        // mənfi çıxmasın
        return Math.max(0, total);
    }

    @Override
    public AttendanceStatusDto getAttendanceStatus(Long employeeId, LocalDate date) {

        TotalWorkHourEntity totalEntity = totalWorkHourRepository
                .findByEmployeeIdAndWorkDate(employeeId, date)
                .orElseThrow(() ->
                        new RuntimeException("Total work not calculated for this employee and date")
                );

        // 2Loglar yalnız status (firstIn/lastOut, adjustments) üçündür
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEndExclusive = date.plusDays(1).atStartOfDay();

        List<BiometricLogEntity> logs = biometricLogRepository
                .findByEmployeeIdAndDateTimeBetween(
                        employeeId,
                        dayStart,
                        dayEndExclusive.minusNanos(1)
                );

        if (logs.isEmpty()) {
            // total var, amma loglar silinibsə – yenə də total qaytaracagiq
            return AttendanceStatusDto.builder()
                    .firstIn(totalEntity.getFirstIn())
                    .lastOut(totalEntity.getLastOut())
                    .totalWorkedMinutes(
                            totalEntity.getTotalWorkMinutes() != null
                                    ? totalEntity.getTotalWorkMinutes()
                                    : 0
                    )
                    .build();
        }

        logs.sort(Comparator.comparing(BiometricLogEntity::getDateTime));

        LocalDateTime firstIn = logs.stream()
                .filter(l -> l.getInOut() == InOutType.IN)
                .map(BiometricLogEntity::getDateTime)
                .min(Comparator.naturalOrder())
                .orElse(totalEntity.getFirstIn());

        LocalDateTime lastOut = logs.stream()
                .filter(l -> l.getInOut() == InOutType.OUT)
                .map(BiometricLogEntity::getDateTime)
                .max(Comparator.naturalOrder())
                .orElse(totalEntity.getLastOut());

        // 3Shift yalnız adjustments üçün lazımdır
        WorkShiftDto shift = employeeClient.getEmployeeShift(employeeId);
        if (shift == null || shift.getStartTime() == null || shift.getEndTime() == null) {
            throw new RuntimeException("Employee shift not found or invalid");
        }

        LocalDateTime shiftStart = LocalDateTime.of(date, shift.getStartTime());
        LocalDateTime shiftEnd = LocalDateTime.of(date, shift.getEndTime());

        // gece smeni olarsa
        if (!shiftEnd.isAfter(shiftStart)) {
            shiftEnd = shiftEnd.plusDays(1);
        }

        //  DTO — total artıq DB-dəndir, burada HESABLANMIR
        AttendanceStatusDto dto = AttendanceStatusDto.builder()
                .firstIn(firstIn)
                .lastOut(lastOut)
                .totalWorkedMinutes(
                        totalEntity.getTotalWorkMinutes() != null
                                ? totalEntity.getTotalWorkMinutes()
                                : 0
                )
                .build();

        // Adjustments
        if (firstIn != null) {
            long lateMinutes = Duration.between(shiftStart, firstIn).toMinutes();
            if (lateMinutes > 0) dto.addAdjustment("lateMinutes", lateMinutes);

            long earlyArrivalMinutes = Duration.between(firstIn, shiftStart).toMinutes();
            if (earlyArrivalMinutes > 0) dto.addAdjustment("earlyArrivalMinutes", earlyArrivalMinutes);
        }

        if (lastOut != null) {
            long earlyLeaveMinutes = Duration.between(lastOut, shiftEnd).toMinutes();
            if (earlyLeaveMinutes > 0) dto.addAdjustment("earlyLeaveMinutes", earlyLeaveMinutes);

            long lateLeaveMinutes = Duration.between(shiftEnd, lastOut).toMinutes();
            if (lateLeaveMinutes > 0) dto.addAdjustment("lateLeaveMinutes", lateLeaveMinutes);
        }

        return dto;
    }

}
