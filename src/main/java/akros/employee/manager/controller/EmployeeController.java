package akros.employee.manager.controller;

import akros.employee.manager.dto.EmployeeRequestDto;
import akros.employee.manager.dto.HttpResponseDto;
import akros.employee.manager.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Employee Manager")
public class EmployeeController {

    private final EmployeeService service;

    @Operation(
            description = "Get endpoint for all employees",
            summary = "Display all the employees.",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }
    )
    @GetMapping
    public ResponseEntity<List<EmployeeRequestDto>> getAllEmployees() {
        return ResponseEntity.ok(service.findAllEmployees());
    }
    @Operation(
            description = "Get employee by email",
            summary = "The found employee by the given email will be shown.",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    ),
                    @ApiResponse(
                            description = "Not Found",
                            responseCode = "404"
                    )
            }
    )
    @GetMapping("/{email}")
    public ResponseEntity<HttpResponseDto> findEmployeeByEmail(@PathVariable("email") String email) {
        var responseDto = service.findEmployeeByEmail(email);
        return new ResponseEntity<>(responseDto, responseDto.getStatus());
    }

    @Operation(
            description = "Add employee",
            summary = "A new employee will be added into the database",
            responses = {
                    @ApiResponse(
                            description = "Created",
                            responseCode = "201"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    ),
                    @ApiResponse(
                            description = "Not Acceptable",
                            responseCode = "406"
                    ),
                    @ApiResponse(
                            description = "Bad Request",
                            responseCode = "400"
                    )
            }
    )
    @PostMapping
    @PreAuthorize("hasAnyAuthority('user:create')")
    public ResponseEntity<HttpResponseDto> addEmployee(@RequestBody EmployeeRequestDto dto) {
        var responseDto = service.saveEmployee(dto);
        return new ResponseEntity<>(responseDto, responseDto.getStatus());
    }

    @Operation(
            description = "Delete employee by email",
            summary = "The found employee will be deleted from the database. Undo operation is not possible.",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    ),
                    @ApiResponse(
                            description = "Not Found",
                            responseCode = "404"
                    )
            }
    )
    @DeleteMapping("/{email}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<HttpResponseDto> deleteEmployee(@PathVariable("email") String email) {
        var responseDto = service.deleteEmployeeByEmail(email);
        return new ResponseEntity<>(responseDto, responseDto.getStatus());
    }
}
