package com.mideo.salesforce

import com.jayway.restassured.builder.ResponseBuilder
import com.jayway.restassured.specification.RequestSpecification
import com.jayway.restassured.response.Header
import com.sforce.async.BulkConnection
import com.mideo.http.HttpRequestSpecificationBuilder
import com.sforce.soap.partner.PartnerConnection
import spock.lang.Specification


class SalesforceConnectionClientTest extends Specification {

    def "Should return BulkConnection"() {
        given:
            HttpRequestSpecificationBuilder mockHttpRequestSpecBuilder = Mock(HttpRequestSpecificationBuilder)
            RequestSpecification mockRequestSpecification = Mock(RequestSpecification)
            SalesforceConfig config = new SalesforceConfig("http://test.salesforce.com")
                    .clientId("3MVG9_7ddP9KqTzcnteMkjh7zaTQmgPEDY13bQhFRo4MXr9PhbzVZqWtfERXQYZn7UQgLUxzv6BNSwWxPlPWX")
                    .clientSecret("6513759911120645968")
                    .userName("foo@bar.com")
                    .password("test1234")
                    .userToken("b2Sm7wA81TOm6sErbLuYtRrP");
            String mockResponsePayload = "{\"instance_url\": \"test_url.com\", \"access_token\": \"1234567\"}";


        when:
            mockHttpRequestSpecBuilder.build()  >> mockRequestSpecification
            mockRequestSpecification.baseUri("http://test.salesforce.com") >> mockRequestSpecification
            mockRequestSpecification.body(config.toString()) >> mockRequestSpecification
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

    def "Should return PartnerConnection"() {
        given:
            HttpRequestSpecificationBuilder mockHttpRequestSpecBuilder = Mock(HttpRequestSpecificationBuilder)
            RequestSpecification mockRequestSpecification = Mock(RequestSpecification)
            SalesforceConfig config = new SalesforceConfig("http://test.salesforce.com")
                .clientId("3MVG9_7ddP9KqTzcnteMkjh7zaTQmgPEDY13bQhFRo4MXr9PhbzVZqWtfERXQYZn7UQgLUxzv6BNSwWxPlPWX")
                .clientSecret("6513759911120645968")
                .userName("foo@bar.com")
                .password("test1234")
                .userToken("b2Sm7wA81TOm6sErbLuYtRrP");
            String mockResponsePayload = "{\"instance_url\": \"test_url.com\", \"access_token\": \"1234567\"}";


        when:
            mockHttpRequestSpecBuilder.build()  >> mockRequestSpecification
            mockRequestSpecification.baseUri("http://test.salesforce.com") >> mockRequestSpecification
            mockRequestSpecification.body(config.toString()) >> mockRequestSpecification
            mockRequestSpecification.header(new Header("Content-Type", "application/x-www-form-urlencoded")) >> mockRequestSpecification
            mockRequestSpecification.post("/services/oauth2/token") >> new ResponseBuilder().setBody(mockResponsePayload).setStatusCode(200).build()
            SalesforceConnectionClient connectionClient = new SalesforceConnectionClient(config, mockHttpRequestSpecBuilder)
            PartnerConnection partnerConnection = connectionClient.getSalesForceWebServicePartnerConnection()

        then:
            assert partnerConnection.config.getRestEndpoint() == "test_url.com/services/async/36.0"
            assert partnerConnection.config.getSessionId() == "1234567"
            assert partnerConnection.config.getAuthEndpoint() == "test_url.com/services/Soap/u/36.0"
            assert partnerConnection.config.getServiceEndpoint() == "test_url.com/services/Soap/u/36.0"
            assert partnerConnection.config.compression
            assert !partnerConnection.config.traceMessage
    }
}

