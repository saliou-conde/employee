package akros.employee.manager.constant;

/**
 * AppConstant for all constants in the application
 */
public final class AppConstant {
    public static final String EMPLOYEE_API_PATH = "/api/v1/employees/";
    public static final String AKROS_USER_API_PATH = "/api/v1/auth/";
    public static final String EMPLOYEE = "employee";
    public static final String AKROS_USER = "user";
    public static final String NOT_FOUND_BY_USERNAME = "User not found by username: ";
    public static final String NOT_ENOUGH_PERMISSION = "Not enough permission: ";
    public static final String[] PUBLIC_URLS = {
            "/actuator/**",
            "/api/v1/auth/register",
            "/api/v1/auth/authenticate",
            "/api/v1/auth/active/**",
            "/api/v1/auth/reset-password/**",
            "/api/v1/auth/change-password/**",
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
