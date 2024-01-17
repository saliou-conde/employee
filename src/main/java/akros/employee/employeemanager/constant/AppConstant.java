package akros.employee.employeemanager.constant;

public class AppConstant {
    public static final String EMPLOYEE_API_PATH = "/api/v1/employees/";
    public static final String AKROS_USER_API_PATH = "/api/v1/auth/";
    public static final String EMPLOYEE = "employee";
    public static final String AKROS_USER = "user";
    public static final String[] PUBLIC_URL = {
            "/actuator/**",
            "/api/v1/auth/register",
            "/api/v1/auth/authenticate",
            "/api/v1/auth/active/**",
            "/v2/api-docs",
            "/configuration/ui",
            "/swagger-resources/**",
            "/webjars/**",
            "/configuration/security",
            "/swagger-ui.html"
    };

    //Swagger
    public static final String API_TITLE = "Employee Manager API";
    public static final String APT_DESCRIPTION = "This API can be used to manage actions and information for an Employee";
    public static final String API_VERSION = "V1.0";
    public static final String API_CONTACT = "V1.0";
    public static final String API_MAIL = "V1.0";
    public static final String CONTACT_NAME = "V1.0";
    public static final String SECURITY_REFERENCE = "V1.0";
    public static final String AUTHORIZATION_SCOPE = "V1.0";
    public static final String AUTHORIZATION_DESCRIPTION = "V1.0";
}
