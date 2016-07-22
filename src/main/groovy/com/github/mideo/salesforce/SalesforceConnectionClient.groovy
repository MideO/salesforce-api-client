package com.github.mideo.salesforce;

import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response
import com.jayway.restassured.specification.RequestSpecification;
import com.sforce.async.AsyncApiException;
import com.sforce.async.BulkConnection
import com.sforce.soap.apex.SoapConnection;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig
import groovy.json.JsonSlurper;


public class SalesforceConnectionClient {

    static BulkConnection salesForceWebServiceBulkConnection;
    static PartnerConnection salesForceWebServicePartnerConnection;
    static SoapConnection salesforceSoapConnection;
    final RequestSpecification requestSpecification;
    private SalesforceConfig salesforceConfig;
    private restExplorerEndpoint;
    private sessionToken;



    public SalesforceConnectionClient(SalesforceConfig salesforceConfig, RequestSpecification requestSpecification) {
        this.salesforceConfig = salesforceConfig;
        this.requestSpecification = requestSpecification;
    }


    Object getSalesforceSession() {
        Response response = requestSpecification.baseUri(salesforceConfig.loginUrl)
                .body(salesforceConfig.toString())
                .header(new Header("Content-Type", "application/x-www-form-urlencoded"))
                .post("/services/oauth2/token");
        Object session = new JsonSlurper().parseText(response.print());
        sessionToken = session.access_token;
        restExplorerEndpoint  = "${session.instance_url}/services/data/v${salesforceConfig.version}";
        return session;

    }

    private ConnectorConfig getConnectorConfig(String apiSoapPath) {
        def session = getSalesforceSession();

        ConnectorConfig connectorConfig = new ConnectorConfig(
                restEndpoint: "${session.instance_url}/services/async/${salesforceConfig.version}",
                serviceEndpoint: "${session.instance_url}${apiSoapPath}${salesforceConfig.version}",
                authEndpoint: "${session.instance_url}${apiSoapPath}${salesforceConfig.version}",
                sessionId: session.access_token,
                compression: true,
                traceMessage: false
        );

        return connectorConfig;
    }

    String getRestExplorerEndpoint() {
        if(restExplorerEndpoint == null){
            getSalesforceSession();
        }
        return restExplorerEndpoint
    }

    String getSessionToken(){
        if(restExplorerEndpoint == null){
            getSalesforceSession();
        }
        return sessionToken;
    }

    BulkConnection getSalesForceWebServiceBulkConnection() throws AsyncApiException {
        if (salesForceWebServiceBulkConnection == null) {
            ConnectorConfig config = getConnectorConfig("/services/Soap/s/");
            salesForceWebServiceBulkConnection = new BulkConnection(config);
        }
        return salesForceWebServiceBulkConnection;
    }

    PartnerConnection getSalesForceWebServicePartnerConnection() throws ConnectionException {
        if (salesForceWebServicePartnerConnection == null) {
            ConnectorConfig config = getConnectorConfig("/services/Soap/u/");
            salesForceWebServicePartnerConnection = new PartnerConnection(config);
        }
        return salesForceWebServicePartnerConnection;
    }

    SoapConnection getSalesforceSoapConnection(){
        if (salesforceSoapConnection == null) {
            ConnectorConfig config = getConnectorConfig("/services/Soap/s/");
            salesforceSoapConnection = new SoapConnection(config);
        }
        return salesforceSoapConnection

    }


}
