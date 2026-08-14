package org.course.ecommerce.service;

import org.course.ecommerce.dto.EmployeeDto;
import org.course.ecommerce.entity.Employee;
import org.course.ecommerce.exception.NotFoundException;
import org.course.ecommerce.mapper.EmployeeMapper;
import org.course.ecommerce.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional
    public EmployeeDto createEmployee(EmployeeDto employee) {
        validate(employee);

        Employee entity = EmployeeMapper.toEntity(employee);
        entity.setEmployeeId(0);
        Employee saved = employeeRepository.save(entity);

        employee.setEmployeeId(saved.getEmployeeId());
        return employee;
    }

    @Override
    @Transactional
    public void updateEmployee(EmployeeDto employee) {
        if (employee == null || employee.getEmployeeId() <= 0)
            throw new IllegalArgumentException("Valid employee ID required for update");

        Employee existing = employeeRepository.findById(employee.getEmployeeId())
                .orElseThrow(() -> new NotFoundException("Employee not found for update"));

        EmployeeMapper.mergeIntoEntity(employee, existing);
        employeeRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteEmployee(int employeeId) {
        if (employeeId <= 0)
            throw new IllegalArgumentException("Valid employee ID required for delete");

        if (!employeeRepository.existsById(employeeId))
            throw new NotFoundException("Employee not found for deletion");

        employeeRepository.deleteById(employeeId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EmployeeDto> getEmployeeById(int employeeId) {
        if (employeeId <= 0) return Optional.empty();

        return employeeRepository.findById(employeeId).map(EmployeeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDto> getAllEmployees() {
        return EmployeeMapper.toDtoList(employeeRepository.findAll());
    }

    private void validate(EmployeeDto employee) {
        if (employee == null)
            throw new IllegalArgumentException("Employee must not be null");

        if (employee.getFirstName() == null || employee.getFirstName().isBlank())
            throw new IllegalArgumentException("Employee first name is required");

        if (employee.getLastName() == null || employee.getLastName().isBlank())
            throw new IllegalArgumentException("Employee last name is required");
    }
}
