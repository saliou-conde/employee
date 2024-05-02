package akros.employee.manager.controller;

import akros.employee.manager.dto.EmployeeResponseDto;
import akros.employee.manager.dto.LoginRequestDto;
import akros.employee.manager.service.AkrosUserService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AkrosUserService service;

    @PostMapping("/register")
    public ResponseEntity<EmployeeResponseDto> register(@RequestBody LoginRequestDto requestDto) {
        log.info("Starting register()");
        EmployeeResponseDto register = service.register(requestDto);
        log.info("Started register()");
        return new ResponseEntity<>(register, register.getStatus());
    }

    @PostMapping("/authenticate")
    public ResponseEntity<EmployeeResponseDto> authenticate(@RequestBody LoginRequestDto requestDto) {
        log.info("Starting authenticate()");
        EmployeeResponseDto authenticate = service.authenticate(requestDto);
        log.info("Started authenticate()");
        return new ResponseEntity<>(authenticate, authenticate.getStatus());
    }

    @Hidden
    @PostMapping("/active/{username}")
    public ResponseEntity<EmployeeResponseDto> active(@PathVariable("username") String username) {
        log.info("Starting active()");
        EmployeeResponseDto authenticate = service.active(username);
        log.info("Started active()");
        return new ResponseEntity<>(authenticate, authenticate.getStatus());
    }

    @PostMapping("/find/{email}")
    public ResponseEntity<EmployeeResponseDto> findByEmail(@PathVariable("email") String email) {
        log.info("Starting findByEmail()");
        EmployeeResponseDto authenticate = service.findByEmail(email);
        log.info("Started findByEmail()");
        return new ResponseEntity<>(authenticate, authenticate.getStatus());
    }

}
