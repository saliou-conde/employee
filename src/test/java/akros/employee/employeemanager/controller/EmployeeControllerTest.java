package akros.employee.employeemanager.controller;

import akros.employee.employeemanager.config.SecurityConfig;
import akros.employee.employeemanager.constant.AppConstant;
import akros.employee.employeemanager.domain.dto.HttpRequestDto;
import akros.employee.employeemanager.domain.dto.HttpResponseDto;
import akros.employee.employeemanager.service.EmployeeService;
import akros.employee.employeemanager.service.impl.TokenService;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Base64;
import java.util.UUID;

import static akros.employee.employeemanager.constant.AppConstant.EMPLOYEE;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({SecurityConfig.class, TokenService.class})
class EmployeeControllerTest {

    @LocalServerPort
    private Integer port;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:14-alpine"
    );

    @Autowired
    private EmployeeService<HttpRequestDto, HttpResponseDto> service;

    @Autowired
    private TestRestTemplate restTemplate;
    private final String PATH = "/api/v1/employees";
    private String token;

    @BeforeEach
    void setUp() {

        HttpHeaders headers = new HttpHeaders();
        // create auth credentials
        String authStr = "saliou:password";
        String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes());
        headers.add(AUTHORIZATION, "Basic " + base64Creds);

        RestAssured.baseURI = "http://localhost:" + port;
        service.deleteAllEmployees();
        var response = restTemplate.exchange(PATH +"/token", HttpMethod.POST, new HttpEntity<>(headers), String.class);
        token = response.getBody();
    }

    @Test
    void should_add_employee() {
        //Given
        var requestDto = new HttpRequestDto();
        requestDto.setEmployeeId(UUID.randomUUID().toString());
        requestDto.setEmail("saliou-conde@gmx.de");
        requestDto.setFirstname("Saliou");
        requestDto.setLastname("Condé");
        requestDto.setPassword("19A12iou#");
        requestDto.setJobCode(UUID.randomUUID().toString());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);;

        //When
        var response = restTemplate.exchange(PATH, HttpMethod.POST, new HttpEntity<>(requestDto, headers), HttpResponseDto.class);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().value()).isEqualTo(CREATED.value());
    }

    @Test
    void should_not_add_employee_by_already_existing_mail() {
        //Given
        var requestDto = new HttpRequestDto(UUID.randomUUID().toString(), "Saliou", "Condé", "saliou-conde@gmx.de", UUID.randomUUID().toString(), "19A12iou#");
        service.saveEmployee(requestDto);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        //When
        var response = restTemplate.exchange(PATH, HttpMethod.POST, new HttpEntity<>(requestDto, headers), HttpResponseDto.class);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().value()).isEqualTo(NOT_ACCEPTABLE.value());
        var responseDto = response.getBody();
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getData()).isNotNull();
    }

    @Test
    void should_not_add_employee_by_invalid_password() {
        //Given
        var requestDto = new HttpRequestDto(UUID.randomUUID().toString(), "Saliou", "Condé", "saliou-conde@gmx.de", UUID.randomUUID().toString(), null);
        service.saveEmployee(requestDto);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        //When
        var response = restTemplate.exchange(PATH, HttpMethod.POST, new HttpEntity<>(requestDto, headers), HttpResponseDto.class);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().value()).isEqualTo(BAD_REQUEST.value());
        var responseDto = response.getBody();
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getData()).isNotNull();
    }

    @Test
    void should_get_all_employees() {
        //Given
        var requestDto = new HttpRequestDto();
        requestDto.setEmployeeId(UUID.randomUUID().toString());
        requestDto.setEmail("saliou-conde@gmx.de");
        requestDto.setFirstname("Saliou");
        requestDto.setLastname("Condé");
        requestDto.setPassword("19A12iou#");
        requestDto.setJobCode(UUID.randomUUID().toString());
        service.saveEmployee(requestDto);

        requestDto.setEmployeeId(UUID.randomUUID().toString());
        requestDto.setEmail("aliou-conde@gmx.de");
        requestDto.setFirstname("Aliou");
        requestDto.setLastname("Condé");
        requestDto.setPassword("19A12iou#");
        requestDto.setJobCode(UUID.randomUUID().toString());
        service.saveEmployee(requestDto);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        //Then
        given()
                .contentType(JSON)
                .headers(headers)
                .when()
                .get(PATH)
                .then()
                .statusCode(OK.value())
                .body(".", hasSize(2));
    }

    @Test
    void should_find_employee_by_email() {
        //Given
        var requestDto = new HttpRequestDto();
        requestDto.setEmployeeId(UUID.randomUUID().toString());
        requestDto.setEmail("saliou-conde@gmx.de");
        requestDto.setFirstname("Saliou");
        requestDto.setLastname("Condé");
        requestDto.setPassword("19A12iou#");
        requestDto.setJobCode(UUID.randomUUID().toString());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        String appConstantPath = AppConstant.API_PATH;

        //When
        HttpResponseDto responseDto = service.saveEmployee(requestDto);

        //Then
        given()
                .contentType(JSON)
                .when()
                .headers(headers)
                .get(PATH +"/saliou-conde@gmx.de")
                .then()
                .statusCode(OK.value());
        assertThat(responseDto.getData()).isNotNull();
        HttpRequestDto httpRequestDto = (HttpRequestDto) responseDto.getData().get(EMPLOYEE);
        assertThat(httpRequestDto.getEmail()).isEqualTo(requestDto.getEmail());
        assertThat(PATH +"/").isEqualTo(appConstantPath);
    }

    @Test
    void should_not_find_employee_by_email() {
        //Given
        var requestDto = new HttpRequestDto();
        requestDto.setEmployeeId(UUID.randomUUID().toString());
        requestDto.setEmail("saliou-conde@gmx.de");
        requestDto.setFirstname("Saliou");
        requestDto.setLastname("Condé");
        requestDto.setPassword("19A12iou#");
        requestDto.setJobCode(UUID.randomUUID().toString());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        //When
        service.saveEmployee(requestDto);

        //Then
        given()
                .contentType(JSON)
                .headers(headers)
                .when()
                .get(PATH +"/saliou-conde@gmx1.de")
                .then()
                .statusCode(NOT_FOUND.value());

    }

    @Test
    void should_delete_employee_by_email() {
        //Given
        var requestDto = new HttpRequestDto();
        requestDto.setEmployeeId(UUID.randomUUID().toString());
        requestDto.setEmail("saliou-conde@gmx.de");
        requestDto.setFirstname("Saliou");
        requestDto.setLastname("Condé");
        requestDto.setPassword("19A12iou#");
        requestDto.setJobCode(UUID.randomUUID().toString());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        //When
        service.saveEmployee(requestDto);

        //Then
        var validatableResponse = given()
                .contentType(JSON)
                .headers(headers)
                .when()
                .delete(PATH +"/saliou-conde@gmx.de")
                .then()
                .statusCode(OK.value());
        assertThat(validatableResponse).isNotNull();
    }

    @Test
    void should_not_delete_employee_by_email() {
        //Given
        var requestDto = new HttpRequestDto();
        requestDto.setEmployeeId(UUID.randomUUID().toString());
        requestDto.setEmail("saliou-conde@gmx.de");
        requestDto.setFirstname("Saliou");
        requestDto.setLastname("Condé");
        requestDto.setPassword("19A12iou#");
        requestDto.setJobCode(UUID.randomUUID().toString());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        //When
        service.saveEmployee(requestDto);

        //Then
        var validatableResponse = given()
                .contentType(JSON)
                .headers(headers)
                .when()
                .delete(PATH +"/saliou-conde@gmx1.de")
                .then()
                .statusCode(NOT_FOUND.value());
        assertThat(validatableResponse).isNotNull();
    }
}