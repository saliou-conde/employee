package akros.employee.employeemanager.controller;

import akros.employee.employeemanager.domain.dto.EmployeeRequestDto;
import akros.employee.employeemanager.domain.dto.HttpResponseDto;
import akros.employee.employeemanager.service.EmployeeService;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.HttpStatus.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmployeeControllerTest {

    @LocalServerPort
    private Integer port;
    private String apiPath = "/api/v1/employees";

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15-alpine"
    );

    @Autowired
    private EmployeeService<EmployeeRequestDto, HttpResponseDto> service;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EmployeeController controller;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        service.deleteAllEmployees();
    }

    @Test
    void should_add_employee() {
        //Given
        var requestDto = new EmployeeRequestDto();
        requestDto.setEmployeeId(UUID.randomUUID().toString());
        requestDto.setEmail("saliou-conde@gmx.de");
        requestDto.setFirstname("Saliou");
        requestDto.setLastname("Condé");
        requestDto.setPassword("19A12iou#");
        requestDto.setJobCode(UUID.randomUUID().toString());

        //When
        var response = controller.addEmployee(requestDto);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().value()).isEqualTo(OK.value());
    }

    @Test
    void should_not_add_invalid_employee() {
        //Given
        var requestDto = new EmployeeRequestDto(UUID.randomUUID().toString(), "Saliou", "Condé", "saliou-conde@gmx.de", UUID.randomUUID().toString(), null);

        //When
        var response = restTemplate.exchange(apiPath, HttpMethod.POST, new HttpEntity<>(requestDto), EmployeeRequestDto.class);
        assertThat(response.getStatusCode().value()).isEqualTo(INTERNAL_SERVER_ERROR.value());
        var employee = response.getBody();
        assertThat(employee).isNotNull();
        assertThat(employee.getEmployeeId()).isNull();
        assertThat(employee.getEmail()).isNull();
        assertThat(employee.getFirstname()).isNull();
        assertThat(employee.getLastname()).isNull();
        assertThat(employee.getJobCode()).isNull();
    }

    @Test
    void should_get_all_employees() {
        //Given
        var requestDto = new EmployeeRequestDto();
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

        //Then
        given()
                .contentType(JSON)
                .when()
                .get(apiPath)
                .then()
                .statusCode(200)
                .body(".", hasSize(2));
    }

    @Test
    void should_find_employee_by_email() {
        //Given
        var requestDto = new EmployeeRequestDto();
        requestDto.setEmployeeId(UUID.randomUUID().toString());
        requestDto.setEmail("saliou-conde@gmx.de");
        requestDto.setFirstname("Saliou");
        requestDto.setLastname("Condé");
        requestDto.setPassword("19A12iou#");
        requestDto.setJobCode(UUID.randomUUID().toString());

        //When
        service.saveEmployee(requestDto);

        //Then
        given()
                .contentType(JSON)
                .when()
                .get(apiPath+"/saliou-conde@gmx.de")
                .then()
                .statusCode(200);

    }

    @Test
    void should_not_find_employee_by_email() {
        //Given
        var requestDto = new EmployeeRequestDto();
        requestDto.setEmployeeId(UUID.randomUUID().toString());
        requestDto.setEmail("saliou-conde@gmx.de");
        requestDto.setFirstname("Saliou");
        requestDto.setLastname("Condé");
        requestDto.setPassword("19A12iou#");
        requestDto.setJobCode(UUID.randomUUID().toString());

        //When
        service.saveEmployee(requestDto);

        //Then
        given()
                .contentType(JSON)
                .when()
                .get(apiPath+"/saliou-conde@gmx1.de")
                .then()
                .statusCode(404);

    }

    @Test
    void should_delete_employee_by_email() {
        //Given
        var requestDto = new EmployeeRequestDto();
        requestDto.setEmployeeId(UUID.randomUUID().toString());
        requestDto.setEmail("saliou-conde@gmx.de");
        requestDto.setFirstname("Saliou");
        requestDto.setLastname("Condé");
        requestDto.setPassword("19A12iou#");
        requestDto.setJobCode(UUID.randomUUID().toString());

        //When
        service.saveEmployee(requestDto);

        //Then
        var validatableResponse = given()
                .contentType(JSON)
                .when()
                .delete(apiPath+"/saliou-conde@gmx.de")
                .then()
                .statusCode(200);
        assertThat(validatableResponse).isNotNull();
    }

    @Test
    void should_not_delete_employee_by_email() {
        //Given
        var requestDto = new EmployeeRequestDto();
        requestDto.setEmployeeId(UUID.randomUUID().toString());
        requestDto.setEmail("saliou-conde@gmx.de");
        requestDto.setFirstname("Saliou");
        requestDto.setLastname("Condé");
        requestDto.setPassword("19A12iou#");
        requestDto.setJobCode(UUID.randomUUID().toString());

        //When
        service.saveEmployee(requestDto);

        //Then
        var validatableResponse = given()
                .contentType(JSON)
                .when()
                .delete(apiPath+"/saliou-conde@gmx1.de")
                .then()
                .statusCode(404);
        assertThat(validatableResponse).isNotNull();
    }
}