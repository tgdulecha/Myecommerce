package org.course.ecommerce.service;

import org.course.ecommerce.dto.EmployeeDto;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {

    EmployeeDto createEmployee(EmployeeDto employee);

    void updateEmployee(EmployeeDto employee);

    void deleteEmployee(int employeeId);

    Optional<EmployeeDto> getEmployeeById(int employeeId);

    List<EmployeeDto> getAllEmployees();
}
