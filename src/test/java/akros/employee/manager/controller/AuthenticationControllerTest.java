package akros.employee.manager.controller;

import akros.employee.manager.config.SecurityConfig;
import akros.employee.manager.dto.HttpResponseDto;
import akros.employee.manager.dto.LoginRequestDto;
import akros.employee.manager.service.impl.AkrosUserService;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static  org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@Testcontainers
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureTestDatabase(replace = NONE)
@Import({SecurityConfig.class})
class AuthenticationControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:14-alpine"
    );

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AkrosUserService akrosUserService;

    @AfterEach
    void deleteEntities() {
        akrosUserService.deleteAllUsers();
    }

    @BeforeEach
    void setUp() {
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUsername("saliou");
        loginRequestDto.setPassword("12346");
        loginRequestDto.setEmail("saliou-conde@gmx.de");
        loginRequestDto.setFirstname("Saliou");
        loginRequestDto.setLastname("Conde");
        var response = restTemplate.exchange(
                "/api/v1/auth/register",
                HttpMethod.POST,
                new HttpEntity<>(loginRequestDto),
                HttpResponseDto.class);

        assertThat(response.getBody()).isNotNull();
        ResponseEntity<HttpResponseDto> active = restTemplate.exchange(
                "/api/v1/auth/active/saliou",
                HttpMethod.POST,
                null,
                HttpResponseDto.class);
        assertThat(active).isNotNull();

    }

    @Test
    void should_not_authenticate_by_invalid_credentials() {
        // Given
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUsername("non-existing");
        loginRequestDto.setPassword("12346");

        //When
        var response = restTemplate.exchange(
                "/api/v1/auth/authenticate",
                HttpMethod.POST,
                new HttpEntity<>(loginRequestDto),
                HttpResponseDto.class);

        //Then
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(FORBIDDEN);
    }

    @Test
    void should_authenticate() {
        // Given
        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUsername("saliou");
        loginRequestDto.setPassword("12346");

        //When
        var response = restTemplate.exchange(
                "/api/v1/auth/authenticate",
                HttpMethod.POST,
                new HttpEntity<>(loginRequestDto),
                HttpResponseDto.class);

        //Then
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(OK);
    }

    @Test
    void should_not_active_by_non_existing_user() {
        //Given
        String username = "non-existing";

        //When
        ResponseEntity<HttpResponseDto> active = restTemplate.exchange(
                "/api/v1/auth/active/"+username,
                HttpMethod.POST,
                null,
                HttpResponseDto.class);

        //Then
        assertThat(active.getBody()).isNotNull();
        assertThat(active.getBody().getStatus()).isEqualTo(NOT_FOUND);
    }
}