package com.example.employee.model.response;

import com.example.employee.model.enums.HandType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class FingerInfoResponse {
    private Integer fingerIndex; // 1-10
    private HandType hand;
}
