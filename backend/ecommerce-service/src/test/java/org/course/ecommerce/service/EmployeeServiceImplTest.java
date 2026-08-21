package org.course.ecommerce.service;

import org.course.ecommerce.dto.EmployeeDto;
import org.course.ecommerce.entity.Employee;
import org.course.ecommerce.exception.NotFoundException;
import org.course.ecommerce.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    private EmployeeServiceImpl employeeService;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeServiceImpl(employeeRepository);
    }

    private EmployeeDto validEmployee() {
        EmployeeDto dto = new EmployeeDto();
        dto.setFirstName("Jane");
        dto.setLastName("Doe");
        return dto;
    }

    @Test
    void createEmployeeRejectsMissingFirstName() {
        EmployeeDto dto = validEmployee();
        dto.setFirstName(" ");

        assertThatThrownBy(() -> employeeService.createEmployee(dto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createEmployeeRejectsMissingLastName() {
        EmployeeDto dto = validEmployee();
        dto.setLastName(null);

        assertThatThrownBy(() -> employeeService.createEmployee(dto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createEmployeeSavesAndReturnsGeneratedId() {
        when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> {
            Employee entity = inv.getArgument(0);
            entity.setEmployeeId(42);
            return entity;
        });

        EmployeeDto result = employeeService.createEmployee(validEmployee());

        assertThat(result.getEmployeeId()).isEqualTo(42);
    }

    @Test
    void updateEmployeeThrowsWhenIdMissing() {
        EmployeeDto dto = validEmployee();
        dto.setEmployeeId(0);

        assertThatThrownBy(() -> employeeService.updateEmployee(dto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateEmployeeThrowsWhenNotFound() {
        EmployeeDto dto = validEmployee();
        dto.setEmployeeId(5);
        when(employeeRepository.findById(5)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.updateEmployee(dto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void deleteEmployeeThrowsWhenNotFound() {
        when(employeeRepository.existsById(5)).thenReturn(false);

        assertThatThrownBy(() -> employeeService.deleteEmployee(5))
                .isInstanceOf(NotFoundException.class);

        verify(employeeRepository, never()).deleteById(any());
    }

    @Test
    void deleteEmployeeDeletesWhenFound() {
        when(employeeRepository.existsById(5)).thenReturn(true);

        employeeService.deleteEmployee(5);

        verify(employeeRepository).deleteById(5);
    }

    @Test
    void getEmployeeByIdReturnsEmptyForNonPositiveId() {
        assertThat(employeeService.getEmployeeById(0)).isEmpty();
        verifyNoInteractions(employeeRepository);
    }
}
