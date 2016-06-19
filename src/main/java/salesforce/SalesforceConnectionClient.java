package salesforce;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;
import com.sforce.async.AsyncApiException;
import com.sforce.async.BulkConnection;

import com.sforce.ws.ConnectorConfig;
import http.HttpRequestSpecificationBuilder;


class SalesforceConnectionClient {

    private static BulkConnection salesForceWebServiceBulkConnection;
    private final HttpRequestSpecificationBuilder httpRequestSpecificationBuilder;
    private SalesforceConfig salesforceConfig;


    SalesforceConnectionClient(SalesforceConfig salesforceConfig, HttpRequestSpecificationBuilder httpRequestSpecificationBuilder) {
        this.salesforceConfig = salesforceConfig;
        this.httpRequestSpecificationBuilder = httpRequestSpecificationBuilder;
    }


    private JsonPath getSalesforceSession() {
        String formData = buildLoginPayload();
        String oauthPath = "/services/oauth2/token";
        Response response = httpRequestSpecificationBuilder.build().baseUri(salesforceConfig.loginUrl)
                .body(formData)
                .header(new Header("Content-Type", "application/x-www-form-urlencoded"))
                .post(oauthPath);
        return new JsonPath(response.body().asString());
    }

    private String buildLoginPayload() {
        String loginFormTemplate = "grant_type=password&client_id=%s&client_secret=%s&username=%s&password=%s";
        return String.format(loginFormTemplate,
                salesforceConfig.clientId,
                salesforceConfig.clientSecret,
                salesforceConfig.username.replace("@", "%40"),
                salesforceConfig.password + salesforceConfig.token);
    }

    private ConnectorConfig getConnectorConfig() {
        JsonPath salesforceSession = getSalesforceSession();
        ConnectorConfig connectorConfig = new ConnectorConfig();
        String instanceUrl = salesforceSession.getString("instance_url");
        String apiVersion = "36.0";
        String apiAsyncPath = "/services/async/";
        String apiSoapPath = "/services/Soap/s/";

        connectorConfig.setRestEndpoint(instanceUrl + apiAsyncPath + apiVersion);
        connectorConfig.setServiceEndpoint(instanceUrl+ apiSoapPath + apiVersion);
        connectorConfig.setAuthEndpoint(instanceUrl+ apiSoapPath + apiVersion);

        connectorConfig.setSessionId(salesforceSession.getString("access_token"));
        connectorConfig.setCompression(true);
        connectorConfig.setTraceMessage(false);
        return connectorConfig;
    }

    BulkConnection getSalesForceWebServiceBulkConnection() throws AsyncApiException {
        if (salesForceWebServiceBulkConnection == null) {
            salesForceWebServiceBulkConnection = new BulkConnection(getConnectorConfig());
        }
        return salesForceWebServiceBulkConnection;
    }


}
