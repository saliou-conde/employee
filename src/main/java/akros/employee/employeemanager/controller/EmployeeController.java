package akros.employee.employeemanager.controller;

import akros.employee.employeemanager.domain.dto.EmployeeRequestDto;
import akros.employee.employeemanager.domain.dto.HttpResponseDto;
import akros.employee.employeemanager.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {

    private final EmployeeService<EmployeeRequestDto, HttpResponseDto> service;

    @GetMapping
    public ResponseEntity<List<EmployeeRequestDto>> getAllEmployees() {
        return ResponseEntity.ok(service.findAllEmployees());
    }

    @GetMapping("/{email}")
    public ResponseEntity<HttpResponseDto> findEmployeeByEmail(@PathVariable("email") String email) {
        HttpResponseDto responseDto = service.findEmployeeByEmail(email);
        return new ResponseEntity<>(responseDto, responseDto.getStatus());
    }

    @PostMapping
    public ResponseEntity<EmployeeRequestDto> addEmployee(@RequestBody EmployeeRequestDto dto) {
        return ResponseEntity.ok(service.saveEmployee(dto));
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<HttpResponseDto> deleteEmployee(@PathVariable("email") String email) {
        HttpResponseDto responseDto = service.deleteEmployeeByEmail(email);
        return new ResponseEntity<>(responseDto, responseDto.getStatus());
    }
}
