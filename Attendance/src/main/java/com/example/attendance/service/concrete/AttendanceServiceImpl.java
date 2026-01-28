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

        System.out.println("=== AttendanceServiceImpl Debug ===");
        System.out.println("EmployeeId: " + employeeId);
        System.out.println("Date: " + date);

        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.atTime(LocalTime.MAX);

        // 1️⃣ Logs alma
        System.out.println("Fetching biometric logs...");
        List<BiometricLogEntity> logs =
                biometricLogRepository.findByEmployeeIdAndDateTimeBetween(employeeId, dayStart, dayEnd);
        System.out.println("Logs count: " + logs.size());

        if (logs.isEmpty()) {
            throw new RuntimeException("No biometric logs for this date");
        }

        // 2️⃣ first IN / last OUT
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

        System.out.println("First IN: " + firstIn);
        System.out.println("Last OUT: " + lastOut);

        if (firstIn == null || lastOut == null) {
            throw new RuntimeException("Invalid IN/OUT logs");
        }

        // 3️⃣ Shift alma
        System.out.println("Fetching employee shift via Feign...");
        WorkShiftDto shift = employeeClient.getEmployeeShift(employeeId);
        System.out.println("Shift received: " + shift);

        if (shift == null || shift.getStartTime() == null || shift.getEndTime() == null) {
            throw new RuntimeException("Employee shift not found or invalid");
        }

        LocalDateTime shiftStart = LocalDateTime.of(date, shift.getStartTime());
        LocalDateTime shiftEnd = LocalDateTime.of(date, shift.getEndTime());
        System.out.println("Shift start: " + shiftStart + ", Shift end: " + shiftEnd);

        // 4️⃣ Actual start/end
        LocalDateTime actualStart = firstIn.isAfter(shiftStart) ? firstIn : shiftStart;
        LocalDateTime actualEnd = lastOut.isBefore(shiftEnd) ? lastOut : shiftEnd;
        System.out.println("Actual work start: " + actualStart + ", Actual work end: " + actualEnd);

        // 5️⃣ Total minutes
        long totalMinutes = Duration.between(actualStart, actualEnd).toMinutes();
        System.out.println("Total work minutes: " + totalMinutes);

        TotalWorkHourEntity entity = TotalWorkHourEntity.builder()
                .employeeId(employeeId)
                .workDate(date)
                .workStartDate(shiftStart)
                .workEndDate(shiftEnd)
                .firstIn(firstIn)
                .lastOut(lastOut)
                .totalWorkMinutes(totalMinutes)
                .build();

        System.out.println("Saving total work hour entity...");
        TotalWorkHourEntity saved = totalWorkHourRepository.save(entity);
        System.out.println("Saved entity ID: " + saved.getId());

        System.out.println("=== End Debug ===");
        return saved;
    }
    @Override
    public AttendanceStatusDto getAttendanceStatus(Long employeeId, LocalDate date) {

        // 1️⃣ Günün başlanğıcı və sonu
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.atTime(LocalTime.MAX);

        // 2️⃣ Gəlmə/çıxma loglarını çəkmək
        List<BiometricLogEntity> logs = biometricLogRepository
                .findByEmployeeIdAndDateTimeBetween(employeeId, dayStart, dayEnd);

        if (logs.isEmpty()) {
            throw new RuntimeException("No logs found for this employee on this date");
        }

        // 3️⃣ İlk giriş və son çıxış
        LocalDateTime firstIn = logs.stream()
                .filter(l -> l.getInOut() == InOutType.IN)
                .map(BiometricLogEntity::getDateTime)
                .min(Comparator.naturalOrder())
                .orElseThrow(() -> new RuntimeException("No IN log found"));

        LocalDateTime lastOut = logs.stream()
                .filter(l -> l.getInOut() == InOutType.OUT)
                .map(BiometricLogEntity::getDateTime)
                .max(Comparator.naturalOrder())
                .orElseThrow(() -> new RuntimeException("No OUT log found"));

        // 4️⃣ İşçinin smenini Employee service-dən çəkmək
        WorkShiftDto shift = employeeClient.getEmployeeShift(employeeId);
        LocalDateTime shiftStart = LocalDateTime.of(date, shift.getStartTime());
        LocalDateTime shiftEnd = LocalDateTime.of(date, shift.getEndTime());

        // 5️⃣ Faktiki iş saatı (shift ilə məhdudlaşdırılmış)
        LocalDateTime actualStart = firstIn.isAfter(shiftStart) ? firstIn : shiftStart;
        LocalDateTime actualEnd = lastOut.isBefore(shiftEnd) ? lastOut : shiftEnd;
        long totalWorkedMinutes = Duration.between(actualStart, actualEnd).toMinutes();

        // 6️⃣ DTO yaratmaq (builder pattern ilə)
        AttendanceStatusDto dto = AttendanceStatusDto.builder()
                .firstIn(firstIn)
                .lastOut(lastOut)
                .totalWorkedMinutes(totalWorkedMinutes)
                .build();

        // 🔹 Gecikmə (Late Arrival)
        long lateMinutes = Duration.between(shiftStart, firstIn).toMinutes();
        if (lateMinutes > 0) dto.addAdjustment("lateMinutes", lateMinutes);

        // 🔹 Erkən gəlmə (Early Arrival)
        long earlyArrivalMinutes = Duration.between(firstIn, shiftStart).toMinutes();
        if (earlyArrivalMinutes > 0) dto.addAdjustment("earlyArrivalMinutes", earlyArrivalMinutes);

        // 🔹 Tez çıxma (Early Leave)
        long earlyLeaveMinutes = Duration.between(lastOut, shiftEnd).toMinutes();
        if (earlyLeaveMinutes > 0) dto.addAdjustment("earlyLeaveMinutes", earlyLeaveMinutes);

        // 🔹 Gec çıxma (Late Leave)
        long lateLeaveMinutes = Duration.between(shiftEnd, lastOut).toMinutes();
        if (lateLeaveMinutes > 0) dto.addAdjustment("lateLeaveMinutes", lateLeaveMinutes);

        return dto;
    }

}
