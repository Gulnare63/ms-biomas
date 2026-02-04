package com.example.employee.model.response;

import com.example.employee.model.enums.StructureType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StructureResponse {
    private Long id;
    private String name;
    private StructureType type;
    private Long parentId;
}
