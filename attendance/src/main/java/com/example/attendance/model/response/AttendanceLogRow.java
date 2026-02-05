package com.example.attendance.model.response;

import com.example.attendance.model.enums.InOutType;
import com.example.attendance.model.enums.VerifyType;

import java.time.LocalDateTime;

public interface AttendanceLogRow {

    String getName();
    String getSurname();

    VerifyType getVerify();
    LocalDateTime getDateTime();


    InOutType getInOut();

    String getDeviceName();
    String getDevicePlaceName();
}
