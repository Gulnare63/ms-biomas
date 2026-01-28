//package com.example.employee.mapper;
//
//import com.example.employee.dao.entity.EmployeeEntity;
//import com.example.employee.dao.entity.StructureEntity;
//import com.example.employee.model.request.EmployeeSaveRequest;
//import com.example.employee.model.response.EmployeeDetailResponse;
//import com.example.employee.model.response.EmployeeListResponse;
//import org.mapstruct.*;
//import java.util.ArrayList;
//
//@Mapper(componentModel = "spring")
//public interface EmployeeMapper {
//
//    // LIST RESPONSE - frontend üçün null-safe
//    @Mapping(target = "userId", source = "id")
//    @Mapping(target = "personalNumber", source = "personalCode", defaultExpression = "java(\"\")")
//    @Mapping(target = "name", defaultExpression = "java(\"\")")
//    @Mapping(target = "surname", defaultExpression = "java(\"\")")
//    @Mapping(target = "middleName", defaultExpression = "java(\"\")")
//    @Mapping(target = "structureName", expression = "java(employee.getStructure() != null ? employee.getStructure().getName() : \"\")")
//    @Mapping(target = "duty", defaultExpression = "java(\"\")")
//    @Mapping(target = "hasCard", expression = "java(employee.getCards() != null && !employee.getCards().isEmpty())")
//    @Mapping(target = "hasFace", expression = "java(employee.getPhoto() != null)")
//    @Mapping(target = "hasFinger", expression = "java(employee.getFingers() != null && !employee.getFingers().isEmpty())")
//    EmployeeListResponse toListResponse(EmployeeEntity employee);
//
//    // DETAIL RESPONSE - null-safe
//    @Mapping(target = "userId", source = "id")
//    @Mapping(target = "personalNumber", source = "personalCode", defaultExpression = "java(\"\")")
//    @Mapping(target = "name", defaultExpression = "java(\"\")")
//    @Mapping(target = "surname", defaultExpression = "java(\"\")")
//    @Mapping(target = "middleName", defaultExpression = "java(\"\")")
//    @Mapping(target = "structureName", expression = "java(employee.getStructure() != null ? employee.getStructure().getName() : \"\")")
//    @Mapping(target = "duty", defaultExpression = "java(\"\")")
//    @Mapping(target = "card", expression = "java(employee.getCards() != null && !employee.getCards().isEmpty() ? employee.getCards().get(0) : null)")
//    @Mapping(target = "employeeFingers", expression = "java(employee.getFingers() != null ? employee.getFingers() : new ArrayList<>())")
//    @Mapping(target = "employeeFingerDataList", expression = "java(new ArrayList<>())") // placeholder, implement later
//    @Mapping(target = "face", expression = "java(employee.getPhoto() != null ? employee.getPhoto().getPhotoData() : null)")
//    EmployeeDetailResponse toDetailResponse(EmployeeEntity employee);
//
//    // CREATE - null-safe
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "structure", source = "structure")
//    @Mapping(target = "details", ignore = true)
//    @Mapping(target = "photo", ignore = true)
//    @Mapping(target = "fingers", ignore = true)
//    @Mapping(target = "cards", ignore = true)
//    @Mapping(target = "personalCode", source = "personalNumber")
//    @Mapping(target = "name", defaultExpression = "java(request.getName() != null ? request.getName() : \"\")")
//    @Mapping(target = "surname", defaultExpression = "java(request.getSurname() != null ? request.getSurname() : \"\")")
//    @Mapping(target = "middleName", defaultExpression = "java(request.getMiddleName() != null ? request.getMiddleName() : \"\")")
//    @Mapping(target = "duty", defaultExpression = "java(request.getDuty() != null ? request.getDuty() : \"\")")
//    EmployeeEntity toEntity(EmployeeSaveRequest request, StructureEntity structure);
//
//    // UPDATE - null-safe
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "structure", source = "structure")
//    @Mapping(target = "personalCode", source = "personalNumber")
//    @Mapping(target = "name", expression = "java(request.getName() != null ? request.getName() : employee.getName())")
//    @Mapping(target = "surname", expression = "java(request.getSurname() != null ? request.getSurname() : employee.getSurname())")
//    @Mapping(target = "middleName", expression = "java(request.getMiddleName() != null ? request.getMiddleName() : employee.getMiddleName())")
//    @Mapping(target = "duty", expression = "java(request.getDuty() != null ? request.getDuty() : employee.getDuty())")
//    void updateEntity(
//            @MappingTarget EmployeeEntity employee,
//            EmployeeSaveRequest request,
//            StructureEntity structure
//    );
//}
