package com.example.attendance.mapper;

import com.example.attendance.dao.entity.TotalWorkHourEntity;
import com.example.attendance.model.response.TotalWorkHourResponse;

public class TotalWorkHourMapper {

    public static TotalWorkHourResponse toResponse(TotalWorkHourEntity entity) {
        return TotalWorkHourResponse.builder()
                .employeeId(entity.getEmployeeId())
                .workDate(entity.getWorkDate())
                .workStartDate(entity.getWorkStartDate())
                .workEndDate(entity.getWorkEndDate())
                .firstIn(entity.getFirstIn())
                .lastOut(entity.getLastOut())
                .totalWorkMinutes(entity.getTotalWorkMinutes())
                .build();
    }
}
