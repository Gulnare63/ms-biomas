package com.example.attendance.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceStatusDto {

    private LocalDateTime firstIn;
    private LocalDateTime lastOut;
    private long totalWorkedMinutes;


    @Builder.Default
    private Map<String, Long> adjustments = new HashMap<>();


    public void addAdjustment(String key, long value) {
        if (value > 0) {
            adjustments.put(key, value);
        }
    }
}
