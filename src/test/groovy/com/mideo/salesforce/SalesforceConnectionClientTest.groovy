package com.mideo.salesforce

import com.jayway.restassured.builder.ResponseBuilder
import com.jayway.restassured.specification.RequestSpecification
import com.jayway.restassured.response.Header
import com.sforce.async.BulkConnection
import com.mideo.http.HttpRequestSpecificationBuilder
import spock.lang.Specification


class SalesforceConnectionClientTest extends Specification {

    def "Should return BulkConnection"() {
        given:
            HttpRequestSpecificationBuilder mockHttpRequestSpecBuilder = Mock(HttpRequestSpecificationBuilder)
            RequestSpecification mockRequestSpecification = Mock(RequestSpecification)
            SalesforceConfig config = new SalesforceConfig(
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


        when:
            mockHttpRequestSpecBuilder.build()  >> mockRequestSpecification
            mockRequestSpecification.baseUri("http://test.salesforce.com") >> mockRequestSpecification
            mockRequestSpecification.body(formData) >> mockRequestSpecification
            mockRequestSpecification.header(new Header("Content-Type", "application/x-www-form-urlencoded")) >> mockRequestSpecification
            mockRequestSpecification.post("/services/oauth2/token") >> new ResponseBuilder().setBody(mockResponsePayload).setStatusCode(200).build()
            SalesforceConnectionClient connectionClient = new SalesforceConnectionClient(config, mockHttpRequestSpecBuilder)
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
