package com.github.mideo.salesforce

import com.sforce.ws.ConnectorConfig
import spock.lang.Specification


class SalesforceConnectionClientTest extends Specification {

    def config
    def mockResponsePayload
    SalesforceConnectionClient connectionClient
    ConnectorConfig connectorConfig

    def setup(){

        config = new SalesforceConfig("http://test.salesforce.com")
                .userName("foo@bar.com")
                .passwordAndToken("test1234b2Sm7wA81TOm6sErbLuYtRrP")
                .apiVersion(36.0);
        mockResponsePayload = "{\"instance_url\": \"test_url.com\", \"access_token\": \"1234567\"}";

        connectionClient = Spy(SalesforceConnectionClient, constructorArgs: [config])
        connectionClient.instanceUrl = "test_url.com"
        connectorConfig = new ConnectorConfig(
                sessionId: "1234567",
                authEndpoint: "test_url.com/services/Soap/s/36.0",
                restEndpoint: "test_url.com/services/async/36.0",
                serviceEndpoint: "test_url.com/services/Soap/s/36.0"
        )

    }

    def "Should return BulkConnection"() {
        when:
            connectionClient.getConnectorConfig(_) >> connectorConfig
            def bulkConnection = connectionClient.getSalesForceWebServiceBulkConnection()

        then:
            assert bulkConnection.config.getRestEndpoint() == "test_url.com/services/async/36.0"
            assert bulkConnection.config.getSessionId() == "1234567"
            assert bulkConnection.config.getAuthEndpoint() == "test_url.com/services/Soap/s/36.0"
            assert bulkConnection.config.getServiceEndpoint() == "test_url.com/services/Soap/s/36.0"
            assert bulkConnection.config.compression
            assert !bulkConnection.config.traceMessage
    }

    def "Should return PartnerConnection"() {
        when:
            connectorConfig.serviceEndpoint = "test_url.com/services/Soap/u/36.0"
            connectorConfig.authEndpoint = "test_url.com/services/Soap/u/36.0"

            connectionClient.getConnectorConfig(_) >> connectorConfig
            def partnerConnection = connectionClient.getSalesForceWebServicePartnerConnection()

        then:
            assert partnerConnection.config.getRestEndpoint() == "test_url.com/services/async/36.0"
            assert partnerConnection.config.getSessionId() == "1234567"
            assert partnerConnection.config.getAuthEndpoint() == "test_url.com/services/Soap/u/36.0"
            assert partnerConnection.config.getServiceEndpoint() == "test_url.com/services/Soap/u/36.0"
            assert partnerConnection.config.compression
            assert !partnerConnection.config.traceMessage
    }

    def "Should return SoapConnection"() {

        when:
            connectionClient.getConnectorConfig(_) >> connectorConfig
            def soapConnection = connectionClient.getSalesforceSoapConnection()

        then:
            assert soapConnection.config.getRestEndpoint() == "test_url.com/services/async/36.0"
            assert soapConnection.config.getSessionId() == "1234567"
            assert soapConnection.config.getAuthEndpoint() == "test_url.com/services/Soap/s/36.0"
            assert soapConnection.config.getServiceEndpoint() == "test_url.com/services/Soap/s/36.0"
            assert soapConnection.config.compression
            assert !soapConnection.config.traceMessage
    }
}

