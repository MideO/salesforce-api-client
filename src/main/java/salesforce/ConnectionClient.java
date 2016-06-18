package salesforce;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;
import com.sforce.async.AsyncApiException;
import com.sforce.async.BulkConnection;

import com.sforce.ws.ConnectorConfig;
import http.HttpRequestSpecificationBuilder;


class ConnectionClient {

    private static BulkConnection salesForceWebServiceBulkConnection;
    private final HttpRequestSpecificationBuilder httpRequestSpecificationBuilder;
    private Config config;


    ConnectionClient(Config config, HttpRequestSpecificationBuilder httpRequestSpecificationBuilder) {
        this.config = config;
        this.httpRequestSpecificationBuilder = httpRequestSpecificationBuilder;
    }


    private JsonPath getSalesforceSession() {
        String formData = produceLoginRequest();
        String oauthPath = "/services/oauth2/token";
        Response response = httpRequestSpecificationBuilder.build().baseUri(config.loginUrl)
                .body(formData)
                .header(new Header("Content-Type", "application/x-www-form-urlencoded"))
                .post(oauthPath);
        return new JsonPath(response.body().asString());
    }

    private String produceLoginRequest() {
        String loginFormTemplate = "grant_type=password&client_id=%s&client_secret=%s&username=%s&password=%s";
        return String.format(loginFormTemplate,
                config.clientId,
                config.clientSecret,
                config.username.replace("@", "%40"),
                config.password + config.token);
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
