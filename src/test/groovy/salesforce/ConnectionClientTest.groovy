package salesforce

import com.jayway.restassured.builder.ResponseBuilder
import com.jayway.restassured.specification.RequestSpecification
import com.jayway.restassured.response.Header;
import com.sforce.async.BulkConnection
import http.HttpRequestSpecificationBuilder
import org.mockito.Mockito
import spock.lang.Specification

import static org.mockito.Mockito.when


class ConnectionClientTest extends Specification {
    HttpRequestSpecificationBuilder mockHttpRequestSpecBuilder
    RequestSpecification mockRequestSpecification
    Config config


    void setup() {
        mockHttpRequestSpecBuilder = Mockito.mock(HttpRequestSpecificationBuilder.class);
        mockRequestSpecification = Mockito.mock(RequestSpecification.class);
        config = new Config(
                "http://test.salesforce.com",
                "3MVG9_7ddP9KqTzcnteMkjh7zaTQmgPEDY13bQhFRo4MXr9PhbzVZqWtfERXQYZn7UQgLUxzv6BNSwWxPlPWX",
                "6513759911120645968",
                "foo@bar.com",
                "test1234",
                "b2Sm7wA81TOm6sErbLuYtRrP"
        )
        String formData = String.format("grant_type=password&client_id=%s&client_secret=%s&username=%s&password=%s",
                config.clientId,
                config.clientSecret,
                config.username.replace("@", "%40"),
                config.password + config.token)
        String mockResponsePayload = "{\"instance_url\": \"test_url.com\", \"access_token\": \"1234567\"}";
        when(mockHttpRequestSpecBuilder.build())
                .thenReturn(mockRequestSpecification);

        when(mockRequestSpecification.baseUri("http://test.salesforce.com"))
                .thenReturn(mockRequestSpecification)

        when(mockRequestSpecification.body(formData))
                .thenReturn(mockRequestSpecification)

        when(mockRequestSpecification.header(new Header("Content-Type", "application/x-www-form-urlencoded")))
                .thenReturn(mockRequestSpecification)

        when(mockRequestSpecification.post("/services/oauth2/token"))
                .thenReturn(new ResponseBuilder().setBody(mockResponsePayload).setStatusCode(200).build())

    }

    def "Should return BulKConnection"() {
        given:
            ConnectionClient connectionClient = new ConnectionClient(config, mockHttpRequestSpecBuilder)

        when:
            BulkConnection bulkConnection = connectionClient.getSalesForceWebServiceBulkConnection()

        then:
            assert bulkConnection.config.getRestEndpoint() == "test_url.com/services/async/36.0"
            assert bulkConnection.config.getSessionId() == "1234567"
            assert bulkConnection.config.getAuthEndpoint() == "test_url.com/services/Soap/s/36.0"
            assert bulkConnection.config.getServiceEndpoint() == "test_url.com/services/Soap/s/36.0"
            assert bulkConnection.config.compression
            assert !bulkConnection.config.traceMessage
    }
}

