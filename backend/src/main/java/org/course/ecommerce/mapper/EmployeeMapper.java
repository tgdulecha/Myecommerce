package org.course.ecommerce.mapper;

import org.course.ecommerce.dto.EmployeeDto;
import org.course.ecommerce.entity.Employee;

import java.util.List;
import java.util.stream.Collectors;

public final class EmployeeMapper {

    private EmployeeMapper() {}

    public static EmployeeDto toDto(Employee entity) {
        if (entity == null) return null;

        EmployeeDto dto = new EmployeeDto();
        dto.setEmployeeId(entity.getEmployeeId());
        dto.setLastName(entity.getLastName());
        dto.setFirstName(entity.getFirstName());
        dto.setTitle(entity.getTitle());
        dto.setTitleOfCourtesy(entity.getTitleOfCourtesy());
        dto.setBirthDate(entity.getBirthDate());
        dto.setHireDate(entity.getHireDate());
        dto.setAddress(entity.getAddress());
        dto.setCity(entity.getCity());
        dto.setRegion(entity.getRegion());
        dto.setPostalCode(entity.getPostalCode());
        dto.setCountry(entity.getCountry());
        dto.setHomePhone(entity.getHomePhone());
        dto.setExtension(entity.getExtension());
        dto.setPhoto(entity.getPhoto());
        dto.setNotes(entity.getNotes());
        dto.setPhotoPath(entity.getPhotoPath());

        if (entity.getReportsTo() != null) {
            dto.setReportsToId(entity.getReportsTo());
        }
        if (entity.getManager() != null) {
            dto.setReportsToName(entity.getManager().getFirstName() + " " + entity.getManager().getLastName());
        }

        return dto;
    }

    // CREATE only: builds a brand-new entity from the DTO. Safe here because there's no
    // existing row to lose data from - do NOT reuse this for updates, use mergeIntoEntity instead.
    public static Employee toEntity(EmployeeDto dto) {
        if (dto == null) return null;

        Employee entity = new Employee();
        applyDtoToEntity(dto, entity);
        return entity;
    }

    // UPDATE: apply the DTO's fields onto an already-loaded, managed entity in place,
    // instead of building a new object - avoids nulling out columns the mapper missed or the DTO omitted
    public static void mergeIntoEntity(EmployeeDto dto, Employee existingEntity) {
        if (dto == null || existingEntity == null) return;

        applyDtoToEntity(dto, existingEntity);
    }

    private static void applyDtoToEntity(EmployeeDto dto, Employee entity) {
        entity.setEmployeeId(dto.getEmployeeId());
        entity.setLastName(dto.getLastName());
        entity.setFirstName(dto.getFirstName());
        entity.setTitle(dto.getTitle());
        entity.setTitleOfCourtesy(dto.getTitleOfCourtesy());
        entity.setBirthDate(dto.getBirthDate());
        entity.setHireDate(dto.getHireDate());
        entity.setAddress(dto.getAddress());
        entity.setCity(dto.getCity());
        entity.setRegion(dto.getRegion());
        entity.setPostalCode(dto.getPostalCode());
        entity.setCountry(dto.getCountry());
        entity.setHomePhone(dto.getHomePhone());
        entity.setExtension(dto.getExtension());
        entity.setPhoto(dto.getPhoto());
        entity.setNotes(dto.getNotes());
        entity.setPhotoPath(dto.getPhotoPath());
        entity.setReportsTo(dto.getReportsToId());
    }

    public static List<EmployeeDto> toDtoList(List<Employee> entities) {
        return entities.stream().map(EmployeeMapper::toDto).collect(Collectors.toList());
    }
}
