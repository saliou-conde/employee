package akros.employee.manager.constant;

public class AppConstant {
    public static final String EMPLOYEE_API_PATH = "/api/v1/employees/";
    public static final String AKROS_USER_API_PATH = "/api/v1/auth/";
    public static final String EMPLOYEE = "employee";
    public static final String AKROS_USER = "user";
    public static final String[] PUBLIC_URL = {
            "/actuator/**",
            "/api/v1/auth/register",
            "/api/v1/auth/authenticate",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html"
    };
}
