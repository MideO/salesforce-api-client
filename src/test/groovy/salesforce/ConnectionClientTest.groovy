package salesforce

import spock.lang.Specification


class ConnectionClientTest extends Specification {
    def "GetSalesForceWebServiceBulkConnection should return BulKConnection"() {
        given:
        HttpRequestSpecBuilder httpRequestSpecBuilder = new HttpRequestSpecBuilder();
        Config config = new Config(
                "http://test.salesforce.com",
                "3MVG9_7ddP9KqTzcnteMkjh7zaTQmgPEDY13bQhFRo4MXr9PhbzVZqWtfERXQYZn7UQgLUxzv6BNSwWxPlPWX",
                "6513759911120645968",
                "foo@bar.com",
                "test1234",
                "b2Sm7wA81TOm6sErbLuYtRrP"
        )
    }
}

