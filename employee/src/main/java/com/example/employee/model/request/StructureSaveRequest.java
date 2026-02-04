package com.example.employee.model.request;

import com.example.employee.model.enums.StructureType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StructureSaveRequest {
    private String name;
    private StructureType type;
    private Long parentId;
}
