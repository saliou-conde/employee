package akros.employee.employeemanager.controller;

import akros.employee.employeemanager.domain.dto.HttpRequestDto;
import akros.employee.employeemanager.domain.dto.HttpResponseDto;
import akros.employee.employeemanager.service.EmployeeService;
import akros.employee.employeemanager.service.impl.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {

    private final EmployeeService<HttpRequestDto, HttpResponseDto> service;
    private final TokenService tokenService;

    @GetMapping
    public ResponseEntity<List<HttpRequestDto>> getAllEmployees() {
        return ResponseEntity.ok(service.findAllEmployees());
    }

    @GetMapping("/{email}")
    public ResponseEntity<HttpResponseDto> findEmployeeByEmail(@PathVariable("email") String email) {
        var responseDto = service.findEmployeeByEmail(email);
        return new ResponseEntity<>(responseDto, responseDto.getStatus());
    }

    @PostMapping
    public ResponseEntity<HttpResponseDto> addEmployee(@RequestBody HttpRequestDto dto) {
        var responseDto = service.saveEmployee(dto);
        return new ResponseEntity<>(responseDto, responseDto.getStatus());
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<HttpResponseDto> deleteEmployee(@PathVariable("email") String email) {
        var responseDto = service.deleteEmployeeByEmail(email);
        return new ResponseEntity<>(responseDto, responseDto.getStatus());
    }

    @PostMapping("/token")
    public ResponseEntity<String> token(Authentication authentication) {
        return ResponseEntity.ok(tokenService.getToken(authentication));
    }
}
