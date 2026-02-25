package com.example.employee.dao.repository;

import com.example.employee.dao.entity.EmployeeEntity;
import com.example.employee.model.response.EmployeeListResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long>, JpaSpecificationExecutor<EmployeeEntity> {
    @Query("""
    select new com.example.employee.model.response.EmployeeListResponse(
        e.id,
        e.personalCode,
        e.name,
        e.surname,
        e.middleName,
        coalesce(s.name, ''),
        coalesce(d.name, ''),
        (exists (select 1 from EmpCardsEntity c where c.employee.id = e.id)),
        (case when p is not null and p.status = com.example.employee.model.enums.Status.ACTIVE then true else false end),
        (exists (select 1 from EmpFingersEntity f where f.employee.id = e.id))
    )
    from EmployeeEntity e
    left join e.structure s
    left join e.duty d
    left join e.photo p
    where (:personal is null or e.personalCode = :personal)

and (:name is null or lower(e.name) like lower(concat('%', :name, '%')))
      and (:structureId is null or s.id = :structureId)
    """)
    List<EmployeeListResponse> findAllForList(
            @Param("personal") String personal,
            @Param("name") String name,
            @Param("structureId") Long structureId
    );
}
