package akros.employee.manager.controller;

import akros.employee.manager.config.SecurityConfig;
import akros.employee.manager.constant.AppConstant;
import akros.employee.manager.dto.EmployeeRequestDto;
import akros.employee.manager.dto.EmployeeResponseDto;
import akros.employee.manager.dto.LoginRequestDto;
import akros.employee.manager.service.AkrosUserService;
import akros.employee.manager.service.EmployeeService;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static akros.employee.manager.constant.AppConstant.EMPLOYEE;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({SecurityConfig.class})
class EmployeeControllerTest {

    @LocalServerPort
    private Integer port;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:14-alpine"
    );

    @Autowired
    private EmployeeService service;

    @Autowired
    private TestRestTemplate restTemplate;
    private final String EMPLOYEE_API_PATH = "/api/v1/employees";
    private String token;

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
        String AUTH_API_PATH = "/api/v1/auth";
        EmployeeResponseDto register = restTemplate.exchange(AUTH_API_PATH +"/register", POST, new HttpEntity<>(loginRequestDto), EmployeeResponseDto.class).getBody();
        EmployeeResponseDto active = restTemplate.exchange(AUTH_API_PATH +"/active/saliou", POST, null, EmployeeResponseDto.class).getBody();


        assertThat(register).isNotNull();
        assertThat(active).isNotNull();

        // create auth credentials
        loginRequestDto.setPassword("12346");
        EmployeeResponseDto body = restTemplate.exchange(AUTH_API_PATH +"/authenticate", POST, new HttpEntity<>(loginRequestDto), EmployeeResponseDto.class).getBody();

        assertThat(body).isNotNull();
        token =  body.getToken();

        RestAssured.baseURI = "http://localhost:" + port;
        service.deleteAllEmployees();

    }

    @Test
    void should_add_employee() {
        //Given
        var requestDto = new EmployeeRequestDto();
        requestDto.setEmployeeId(randomUUID().toString());
        requestDto.setEmail("saliou-conde@gmx.de");
        requestDto.setFirstname("Saliou");
        requestDto.setLastname("Condé");
        requestDto.setPassword("19A12iou#");
        requestDto.setUsername(randomUUID().toString());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        //When
        var response = restTemplate.exchange(EMPLOYEE_API_PATH, POST, new HttpEntity<>(requestDto, headers), EmployeeResponseDto.class);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().value()).isEqualTo(CREATED.value());
    }

    @Test
    void should_not_add_employee_by_already_existing_mail() {
        //Given
        var requestDto = new EmployeeRequestDto(
                randomUUID().toString(),
                "Saliou",
                "Condé",
                "saliou-conde@gmx.de",
                "saliou",
                "19A12iou#");
        service.saveEmployee(requestDto);
        var headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        //When
        var response = restTemplate.exchange(
                EMPLOYEE_API_PATH,
                POST,
                new HttpEntity<>(requestDto, headers),
                EmployeeResponseDto.class);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().value()).isEqualTo(NOT_ACCEPTABLE.value());
        var responseDto = response.getBody();
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getData()).isNotNull();
    }

    @Test
    void should_not_add_employee_by_invalid_password() {
        //Given
        var requestDto = new EmployeeRequestDto(
                randomUUID().toString(),
                "Saliou",
                "Condé",
                "saliou-conde@gmx.de",
                randomUUID().toString(),
                null);
        service.saveEmployee(requestDto);
        var headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        //When
        var response = restTemplate.exchange(
                EMPLOYEE_API_PATH,
                POST,
                new HttpEntity<>(requestDto, headers),
                EmployeeResponseDto.class);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().value()).isEqualTo(BAD_REQUEST.value());
        var responseDto = response.getBody();
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getData()).isNotNull();
    }

    @Test
    void should_update_employee() {
        //Given
        var requestDto = new EmployeeRequestDto();
        var employeeId = randomUUID().toString();
        var email = "saliou-conde@gmx.de";
        requestDto.setEmployeeId(employeeId);
        requestDto.setEmail(email);
        requestDto.setFirstname("Saliou");
        requestDto.setLastname("Condé");
        requestDto.setPassword("19A12iou#");
        requestDto.setUsername(randomUUID().toString());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        //When
        var response = service.saveEmployee(requestDto);
        assertThat(response).isNotNull();

        var findEmployee = service.findEmployeeByEmail(email);

        EmployeeRequestDto dto = (EmployeeRequestDto) findEmployee.getData().get(EMPLOYEE);
        assertThat(findEmployee).isNotNull();

        var updatedEmployee =  restTemplate.exchange(EMPLOYEE_API_PATH +"/"+email, HttpMethod.PUT, new HttpEntity<>(dto, headers), EmployeeResponseDto.class);
        assertThat(updatedEmployee).isNotNull();
        assertThat(updatedEmployee.getStatusCode().value()).isEqualTo(OK.value());

    }

    @Test
    void should_not_update_employee() {
        //Given
        var requestDto = new EmployeeRequestDto();
        var employeeId = randomUUID().toString();
        var email = "saliou-conde12333@gmx.de";
        requestDto.setEmployeeId(employeeId);
        requestDto.setEmail(email);
        requestDto.setFirstname("Saliou");
        requestDto.setLastname("Condé");
        requestDto.setPassword("19A12iou#");
        requestDto.setUsername(randomUUID().toString());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        //When
        var updatedEmployee =  restTemplate.exchange(EMPLOYEE_API_PATH +"/"+email, HttpMethod.PUT, new HttpEntity<>(requestDto, headers), EmployeeResponseDto.class);

        //Then
        assertThat(updatedEmployee).isNotNull();
        assertThat(updatedEmployee.getStatusCode().value()).isEqualTo(NOT_FOUND.value());

    }

    @Test
    void should_get_all_employees() {
        //Given
        var requestDto = new EmployeeRequestDto();
        requestDto.setEmployeeId(randomUUID().toString());
        requestDto.setEmail("saliou-conde@gmx.de");
        requestDto.setFirstname("Saliou");
        requestDto.setLastname("Condé");
        requestDto.setPassword("19A12iou#");
        requestDto.setUsername(randomUUID().toString());
        service.saveEmployee(requestDto);

        requestDto.setEmployeeId(randomUUID().toString());
        requestDto.setEmail("aliou-conde@gmx.de");
        requestDto.setFirstname("Aliou");
        requestDto.setLastname("Condé");
        requestDto.setPassword("19A12iou#");
        requestDto.setUsername(randomUUID().toString());
        service.saveEmployee(requestDto);
        var headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        //Then
        given()
                .contentType(JSON)
                .headers(headers)
                .when()
                .get(EMPLOYEE_API_PATH)
                .then()
                .statusCode(OK.value())
                .body(".", hasSize(2));
    }

    @Test
    void should_find_employee_by_email() {
        //Given
        var requestDto = new EmployeeRequestDto();
        requestDto.setEmployeeId(randomUUID().toString());
        requestDto.setEmail("saliou-conde@gmx.de");
        requestDto.setFirstname("Saliou");
        requestDto.setLastname("Condé");
        requestDto.setPassword("19A12iou#");
        requestDto.setUsername(randomUUID().toString());
        var headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        var appConstantPath = AppConstant.EMPLOYEE_API_PATH;

        //When
        var responseDto = service.saveEmployee(requestDto);

        //Then
        given()
                .contentType(JSON)
                .when()
                .headers(headers)
                .get(EMPLOYEE_API_PATH +"/saliou-conde@gmx.de")
                .then()
                .statusCode(OK.value());
        assertThat(responseDto.getData()).isNotNull();
        EmployeeRequestDto employeeRequestDto = (EmployeeRequestDto) responseDto.getData().get(EMPLOYEE);
        assertThat(employeeRequestDto.getEmail()).isEqualTo(requestDto.getEmail());
        assertThat(EMPLOYEE_API_PATH +"/").isEqualTo(appConstantPath);
    }

    @Test
    void should_not_find_employee_by_email() {
        //Given
        var requestDto = new EmployeeRequestDto();
        requestDto.setEmployeeId(randomUUID().toString());
        requestDto.setEmail("saliou-conde@gmx.de");
        requestDto.setFirstname("Saliou");
        requestDto.setLastname("Condé");
        requestDto.setPassword("19A12iou#");
        requestDto.setUsername("saliou");
        var headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        //When
        service.saveEmployee(requestDto);

        //Then
        given()
                .contentType(JSON)
                .when()
                .headers(headers)
                .get(EMPLOYEE_API_PATH +"/saliou-conde12@gmx.de")
                .then()
                .statusCode(NOT_FOUND.value());

    }

    @Test
    void should_delete_employee_by_email() {
        //Given
        var requestDto = new EmployeeRequestDto();
        requestDto.setEmployeeId(randomUUID().toString());
        requestDto.setEmail("saliou-conde@gmx.de");
        requestDto.setFirstname("Saliou");
        requestDto.setLastname("Condé");
        requestDto.setPassword("19A12iou#");
        requestDto.setUsername(randomUUID().toString());
        var headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        //When
        service.saveEmployee(requestDto);

        //Then
        var validatableResponse = given()
                .contentType(JSON)
                .headers(headers)
                .when()
                .delete(EMPLOYEE_API_PATH +"/saliou-conde@gmx.de")
                .then()
                .statusCode(OK.value());
        assertThat(validatableResponse).isNotNull();
    }

    @Test
    void should_not_delete_employee_by_email() {
        //Given
        var requestDto = new EmployeeRequestDto();
        requestDto.setEmployeeId(randomUUID().toString());
        requestDto.setEmail("saliou-conde@gmx.de");
        requestDto.setFirstname("Saliou");
        requestDto.setLastname("Condé");
        requestDto.setPassword("19A12iou#");
        requestDto.setUsername(randomUUID().toString());
        var headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        //When
        service.saveEmployee(requestDto);

        //Then
        var validatableResponse = given()
                .contentType(JSON)
                .headers(headers)
                .when()
                .delete(EMPLOYEE_API_PATH +"/saliou-conde@gmx1.de")
                .then()
                .statusCode(NOT_FOUND.value());
        assertThat(validatableResponse).isNotNull();
    }
}