package salesforce;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;
import com.sforce.async.AsyncApiException;
import com.sforce.async.BulkConnection;

import com.sforce.ws.ConnectorConfig;


class ConnectionClient {

    private static BulkConnection salesForceWebServiceBulkConnection;
    private final HttpRequestSpecBuilder httpRequestSpecBuilder;
    private Config config;


    ConnectionClient(Config config, HttpRequestSpecBuilder httpRequestSpecBuilder) {
        this.config = config;
        this.httpRequestSpecBuilder = httpRequestSpecBuilder;
    }


    private JsonPath getSalesforceSession() {
        String formData = produceLoginRequest();
        String oauthPath = "/services/oauth2/token";
        Response response = httpRequestSpecBuilder.getRequestSpecification().baseUri(config.loginUrl)
                .body(formData)
                .header(new Header("Content-Type", "application/x-www-form-urlencoded"))
                .post(oauthPath);

        return JsonPath.from(response.print());
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
