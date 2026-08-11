package org.course.ecommerce.controller;

import org.course.ecommerce.dto.EmployeeDto;
import org.course.ecommerce.exception.NotFoundException;
import org.course.ecommerce.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

// URL: http://localhost:8081/api/employee
@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<EmployeeDto> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable int id) {
        return employeeService.getEmployeeById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> createEmployee(@RequestBody EmployeeDto employee) {
        EmployeeDto created = employeeService.createEmployee(employee);
        URI location = URI.create("/api/employee/" + created.getEmployeeId());
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateEmployee(@PathVariable int id, @RequestBody EmployeeDto employee) {
        if (id != employee.getEmployeeId())
            return ResponseEntity.badRequest().build();

        if (employeeService.getEmployeeById(id).isEmpty())
            return ResponseEntity.notFound().build();

        employeeService.updateEmployee(employee);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable int id) {
        try {
            employeeService.deleteEmployee(id);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
