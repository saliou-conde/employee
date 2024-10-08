package akros.employee.manager.controller;

import akros.employee.manager.AbstractEmployeeIT;
import akros.employee.manager.config.SecurityConfig;
import akros.employee.manager.dto.EmployeeResponseDto;
import akros.employee.manager.dto.LoginRequestDto;
import akros.employee.manager.service.AkrosUserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;


class AuthenticationControllerIT extends AbstractEmployeeIT {

    @Autowired
    private AkrosUserService akrosUserService;
    private String token;
    private final String AUTH_API_PATH = "/api/v1/auth";

    @AfterEach
    void deleteEntities() {
        akrosUserService.deleteAllUsers();
    }

    @BeforeEach
    void setUp() {
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUsername("saliou");
        loginRequestDto.setPassword("12345");
        loginRequestDto.setEmail("saliou-conde@gmx.de");
        loginRequestDto.setFirstname("Saliou");
        loginRequestDto.setLastname("Conde");
        var response = restTemplate.exchange(
                AUTH_API_PATH + "/register",
                HttpMethod.POST,
                new HttpEntity<>(loginRequestDto),
                EmployeeResponseDto.class);

        assertThat(response.getBody()).isNotNull();
        token = response.getBody().getToken();

        ResponseEntity<EmployeeResponseDto> active = restTemplate.exchange(
                AUTH_API_PATH + "/active/saliou",
                HttpMethod.POST,
                null,
                EmployeeResponseDto.class);
        assertThat(active).isNotNull();

    }

    @Test
    void should_not_authenticate_by_invalid_username() {
        // Given
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUsername("non-existing");
        loginRequestDto.setPassword("12345");

        //When
        var response = restTemplate.exchange(
                AUTH_API_PATH + "/authenticate",
                HttpMethod.POST,
                new HttpEntity<>(loginRequestDto),
                EmployeeResponseDto.class);

        //Then
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(NOT_FOUND);
    }

    @Test
    void should_not_authenticate_by_invalid_password() {
        // Given
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUsername("saliou");
        loginRequestDto.setPassword("12346");

        //When
        var response = restTemplate.exchange(
                AUTH_API_PATH + "/authenticate",
                HttpMethod.POST,
                new HttpEntity<>(loginRequestDto),
                EmployeeResponseDto.class);

        //Then
        assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
    }

    @Test
    void should_authenticate() {
        // Given
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUsername("saliou");
        loginRequestDto.setPassword("12345");

        //When
        var response = restTemplate.exchange(
                AUTH_API_PATH + "/authenticate",
                HttpMethod.POST,
                new HttpEntity<>(loginRequestDto),
                EmployeeResponseDto.class);

        //Then
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(OK);
    }

    @Test
    void should_not_active_by_non_existing_user() {
        //Given
        String username = "non-existing";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        //When
        ResponseEntity<EmployeeResponseDto> active = restTemplate.exchange(
                AUTH_API_PATH + "/active/" + username,
                HttpMethod.POST,
                new HttpEntity<>(headers),
                EmployeeResponseDto.class);

        //Then
        assertThat(active.getBody()).isNotNull();
        assertThat(active.getBody().getStatus()).isEqualTo(NOT_FOUND);
    }
}