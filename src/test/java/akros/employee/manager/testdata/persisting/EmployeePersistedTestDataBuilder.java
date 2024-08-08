package akros.employee.manager.testdata.persisting;

import akros.employee.manager.dto.EmployeeRequestDto;
import akros.employee.manager.testdata.EmployeeTestDataBuilder;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

public class EmployeePersistedTestDataBuilder extends EmployeeTestDataBuilder {
    private final TestEntityManager entityManager;

    public EmployeePersistedTestDataBuilder(TestEntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public <T extends EmployeeRequestDto> T build(T t) {
        return entityManager.persist(t);
    }


}
