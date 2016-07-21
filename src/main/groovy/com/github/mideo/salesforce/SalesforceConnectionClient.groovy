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
    private final String API_VERSION = "36.0";
    private final String API_ASYNC_PATH = "/services/async/";


    public SalesforceConnectionClient(SalesforceConfig salesforceConfig, RequestSpecification requestSpecification) {
        this.salesforceConfig = salesforceConfig;
        this.requestSpecification = requestSpecification;
    }


    Object getSalesforceSession() {
        Response response = requestSpecification.baseUri(salesforceConfig.loginUrl)
                .body(salesforceConfig.toString())
                .header(new Header("Content-Type", "application/x-www-form-urlencoded"))
                .post("/services/oauth2/token");
        return new JsonSlurper().parseText(response.print());

    }

    private ConnectorConfig getConnectorConfig(String apiVersion, String apiAsyncPath, String apiSoapPath) {
        def session = getSalesforceSession();

        ConnectorConfig connectorConfig = new ConnectorConfig(
                restEndpoint: session.instance_url + apiAsyncPath + apiVersion,
                serviceEndpoint: session.instance_url+ apiSoapPath + apiVersion,
                authEndpoint: session.instance_url+ apiSoapPath + apiVersion,
                sessionId: session.access_token,
                compression: true,
                traceMessage: false
        );

        return connectorConfig;
    }

    BulkConnection getSalesForceWebServiceBulkConnection() throws AsyncApiException {
        if (salesForceWebServiceBulkConnection == null) {
            ConnectorConfig config = getConnectorConfig(API_VERSION, API_ASYNC_PATH, "/services/Soap/s/");
            salesForceWebServiceBulkConnection = new BulkConnection(config);
        }
        return salesForceWebServiceBulkConnection;
    }

    PartnerConnection getSalesForceWebServicePartnerConnection() throws ConnectionException {
        if (salesForceWebServicePartnerConnection == null) {
            ConnectorConfig config = getConnectorConfig(API_VERSION, API_ASYNC_PATH, "/services/Soap/u/");
            salesForceWebServicePartnerConnection = new PartnerConnection(config);
        }
        return salesForceWebServicePartnerConnection;
    }

    SoapConnection getSalesforceSoapConnection(){
        if (salesforceSoapConnection == null) {
            ConnectorConfig config = getConnectorConfig(API_VERSION, API_ASYNC_PATH, "/services/Soap/s/");
            salesforceSoapConnection = new SoapConnection(config);
        }
        return salesforceSoapConnection

    }


}
