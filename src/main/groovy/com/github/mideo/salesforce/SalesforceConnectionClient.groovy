package com.github.mideo.salesforce;

import com.jayway.restassured.specification.RequestSpecification;
import com.sforce.async.AsyncApiException;
import com.sforce.async.BulkConnection
import com.sforce.soap.apex.SoapConnection;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig



public class SalesforceConnectionClient {

    final RequestSpecification requestSpecification;
    private SalesforceConfig salesforceConfig;
    String restExplorerEndpoint;
    String instanceUrl;
    static ConnectorConfig sessionConfig
    private sessionToken;



    public SalesforceConnectionClient(SalesforceConfig salesforceConfig, RequestSpecification requestSpecification) {
        this.salesforceConfig = salesforceConfig;
        this.requestSpecification = requestSpecification;
    }


    private void logIn() {
        if (sessionConfig != null) {
            return;
        }

        ConnectorConfig connectorConfig = new ConnectorConfig(
                username: salesforceConfig.user,
                password:salesforceConfig.passwordAndToken,
                authEndpoint: "${salesforceConfig.loginUrl}/services/Soap/u/${salesforceConfig.version}"
        )
        sessionConfig = new PartnerConnection(connectorConfig).config;
        instanceUrl = sessionConfig.serviceEndpoint.split('.com')[0] + '.com'
        restExplorerEndpoint  = "${instanceUrl}/services/data/v${salesforceConfig.version}";
        sessionToken = sessionConfig.sessionId;
    }

    ConnectorConfig getConnectorConfig(String apiSoapPath) {
        logIn();
        return new ConnectorConfig(
                restEndpoint: "${instanceUrl}/services/async/${salesforceConfig.version}",
                serviceEndpoint: "${instanceUrl}${apiSoapPath}${salesforceConfig.version}",
                authEndpoint: "${instanceUrl}${apiSoapPath}${salesforceConfig.version}",
                sessionId: sessionToken,
                compression: true,
                traceMessage: false
        )
    }

    String getRestExplorerEndpoint() {
        if(restExplorerEndpoint == null){
            logIn();
        }
        return restExplorerEndpoint
    }

    String getSessionToken(){
        if(restExplorerEndpoint == null){
            logIn();
        }
        return sessionToken;
    }

    BulkConnection getSalesForceWebServiceBulkConnection() throws AsyncApiException {
        ConnectorConfig config = getConnectorConfig("/services/Soap/s/");
        return new BulkConnection(config);
    }

    PartnerConnection getSalesForceWebServicePartnerConnection() throws ConnectionException {
        ConnectorConfig config = getConnectorConfig("/services/Soap/u/");
        return new PartnerConnection(config);
    }

    SoapConnection getSalesforceSoapConnection(){
        ConnectorConfig config = getConnectorConfig("/services/Soap/s/");
        return new SoapConnection(config);
    }
}
