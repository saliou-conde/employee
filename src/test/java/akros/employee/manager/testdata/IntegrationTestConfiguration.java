package akros.employee.manager.testdata;

import akros.employee.manager.testdata.persisting.EmployeePersistedTestDataBuilder;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
//@Profile("test")
public class IntegrationTestConfiguration {

    @Bean
    @Autowired
    public TestEntityManager entityManager(EntityManagerFactory entityManagerFactory) {
        return new TestEntityManager(entityManagerFactory);
    }

    @Bean
    public EmployeeTestDataBuilder employeeTestDataBuilder() {
        return new EmployeeTestDataBuilder();
    }

    @Bean
    @Autowired
    public EmployeePersistedTestDataBuilder employeePersistedTestDataBuilder(final TestEntityManager entityManager) {
        return new EmployeePersistedTestDataBuilder(entityManager);
    }
}
