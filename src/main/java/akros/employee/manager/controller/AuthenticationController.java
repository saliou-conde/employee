package akros.employee.manager.controller;

import akros.employee.manager.dto.HttpResponseDto;
import akros.employee.manager.dto.LoginRequestDto;
import akros.employee.manager.service.impl.AkrosUserService;
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
    public ResponseEntity<HttpResponseDto> register(@RequestBody LoginRequestDto requestDto) {
        HttpResponseDto register = service.register(requestDto);
        return new ResponseEntity<>(register, register.getStatus());
    }

    @PostMapping("/authenticate")
    public ResponseEntity<HttpResponseDto> authenticate(@RequestBody LoginRequestDto requestDto) {
        HttpResponseDto authenticate = service.authenticate(requestDto);
        return new ResponseEntity<>(authenticate, authenticate.getStatus());
    }

    @Hidden
    @PostMapping("/active/{username}")
    public ResponseEntity<HttpResponseDto> active(@PathVariable("username") String username) {
        HttpResponseDto authenticate = service.active(username);
        return new ResponseEntity<>(authenticate, authenticate.getStatus());
    }

}
