package uk.gov.pay.adminusers.fixtures;

import uk.gov.pay.adminusers.utils.DatabaseTestHelper;

import static java.lang.String.valueOf;
import static org.apache.commons.lang3.RandomUtils.nextInt;

public class ServiceDbFixture {

    private final DatabaseTestHelper databaseHelper;
    private String[] gatewayAccountIds = new String[]{valueOf(nextInt())};
    private Integer id;

    private ServiceDbFixture(DatabaseTestHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public static ServiceDbFixture serviceDbFixture(DatabaseTestHelper databaseTestHelper) {
        return new ServiceDbFixture(databaseTestHelper);
    }

    public ServiceDbFixture withGatewayAccountIds(String... gatewayAccountIds) {
        this.gatewayAccountIds = gatewayAccountIds;
        return this;
    }

    public int insertService() {
        int serviceId = id == null ? nextInt() : id;
        databaseHelper.addService(serviceId, gatewayAccountIds);
        return serviceId;
    }

    public ServiceDbFixture withId(int id) {
        this.id = id;
        return this;
    }
}
