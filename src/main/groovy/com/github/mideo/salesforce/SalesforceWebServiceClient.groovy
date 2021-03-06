package com.github.mideo.salesforce


import com.sforce.async.*
import com.sforce.soap.apex.ExecuteAnonymousResult

import com.sforce.ws.ConnectionException;
import org.apache.commons.lang3.StringUtils


public class SalesforceWebServiceClient {


    private SalesforceConnectionClient salesforceConnectionClient;
    private Job job;
    private Batch batch;

    private SObjectApi sObjectApi;

    /**
     * @param salesforceConnectionClient Initiated Salesforce Connection client
     *                                   <p>
     *                                   <br >Usage:<br >
     *                                   SalesforceConfig connectorConfig = new SalesforceConfig("abc").clientId("wewew").clientSecret("dfdfd").userName("sdsds").password("sdsds").userToken("sdssd");<br >
     *                                   SalesforceConnectionClient connectionClient = new SalesforceConnectionClient(connectorConfig, httpRequestSpecification);<br >
     *                                   SalesforceWebServiceClient webClient = new SalesforceWebServiceClient(connectionClient);<br >
     */
    public SalesforceWebServiceClient(SalesforceConnectionClient salesforceConnectionClient) {
        this.salesforceConnectionClient = salesforceConnectionClient;

        job = new Job(bulkConnection: salesforceConnectionClient.getSalesForceWebServiceBulkConnection());
        batch = new Batch(bulkConnection: salesforceConnectionClient.getSalesForceWebServiceBulkConnection());

        sObjectApi = new SObjectApi(
                partnerConnection: salesforceConnectionClient.getSalesForceWebServicePartnerConnection(),
                soapConnection: salesforceConnectionClient.getSalesforceSoapConnection(),
                restExplorerUrl: salesforceConnectionClient.getRestExplorerEndpoint(),
                sessionToken: salesforceConnectionClient.getSessionToken(),
        )


    }

    /**
     * @param csvInputStream   RFC4180 [double qouted CSV, comma seperated, row ended with `\r\n`] Input String from String or file
     * @param targetObjectName Salesforce Object Name
     * @return PublishResult publish result object
     * @throws AsyncApiException Saleforce Api AsyncApiException
     **/
    public PublishResult publishCsvToTable(InputStream csvInputStream, String targetObjectName) throws AsyncApiException {
        publishDataToTable(csvInputStream, targetObjectName, ContentType.CSV);
    }

    /**
     * @param jsonInputStream input stream from json file
     * @param targetObjectName Salesforce Object Name
     * @return PublishResult publish result object
     * @throws AsyncApiException Saleforce Api AsyncApiException
     **/
    public PublishResult publishJsonToTable(InputStream jsonInputStream, String targetObjectName) throws AsyncApiException {
        publishDataToTable(jsonInputStream, targetObjectName, ContentType.JSON);
    }

    /**
     * @param jsonInputStream input stream from json file
     * @param targetObjectName Salesforce Object Name
     * @return PublishResult publish result object
     * @throws AsyncApiException Saleforce Api AsyncApiException
     **/
    public PublishResult publishXMLToTable(InputStream jsonInputStream, String targetObjectName) throws AsyncApiException {
        publishDataToTable(jsonInputStream, targetObjectName, ContentType.XML);
    }

    /**
     * @param InputStream
     * @param targetObjectName Salesforce Object Name
     * @param contentType com.sforce.async.ContentType
     * @return PublishResult publish result object
     * @throws AsyncApiException Saleforce Api AsyncApiException
     **/
    public PublishResult publishDataToTable(InputStream inputStream, String targetObjectName, ContentType contentType) throws AsyncApiException {
        JobInfo jobInfo = job.newJobInfo(targetObjectName)
                .toInsert(contentType)
                .create();

        PublishResult publishResult = batch.addJob(jobInfo)
                .withInputStream(inputStream)
                .createStream().finaliseJob();

        if (getBatchDataStatus(jobInfo.getId(), batch.batchInfo.id) == BatchStateEnum.Failed.name()) {

            throw new SalesforceApiOperationException("Failed to Publish data: \n" + batch.batchInfo);
        }
        return publishResult

    }


    /**
     * @param apexCode - apex code to execute
     * @return
     */
    public ExecuteAnonymousResult executeApexBlock(String apexCode) {
        return sObjectApi.executeApexBlock(apexCode);
    }

    /**
     * @param sObjectName Salesforce Object Name
     * @return deserializable Object
     * @throws ConnectionException <br >Usage: <br >
     *                             webClient.describeSObject("Account")
     **/
    public Object describe(String sObjectName) throws ConnectionException {
        return sObjectApi.describe(sObjectName);
    }

    /**
     * @param queryString - soql query
     * @return
     */
    public List<Map<String,Object>> executeSoqlQuery(String queryString) {
        return sObjectApi.executeSoqlQuery(queryString);
    }

    /**
     * @param jobId   Salesoforce Job Id
     * @param batchId Saleforce Batch Id
     * @return String value of Status of Published Batch
     * @throws AsyncApiException Saleforce Api AsyncApiException
     **/
    public String getBatchDataStatus(String jobId, String batchId) throws AsyncApiException {
        return batch.getBatchStatus(jobId, batchId);

    }


    /**
     * @param sObjectName Salesforce Object Name
     * @param serializableObject Any serializable object
     * @return String value of created Object Id
     * @throws ConnectionException <br >Usage:<br >
     *                             Map&lt;String, Object&gt; accountDataMap = new HashMap&lt;String, String&gt;();<br>
     *                             accountDataMap.put("Name", "testName Surname");<br>
     *                             accountDataMap.put("email", "testName@example.com");<br><br>
     *                             <p>
     *                             webClient.createObject("Account", accountDataMap)<br>
     *                             <br ><br >Or with a pojo<br >
     *                             class Account {<br >
     *                                 private String name;<br >
     *                                 private String email;<br ><br >
     *
     *                                 public Account(String name, String email){<br >
     *                                      this.name = name;<br >
     *                                      this.email = email;<br >
     *                                 }<br >
     *                             }<br >
     *                             Account account = new Account("testName Surname", "testName@example.com");<br>
     *                             <p>
     *                             webClient.createObject("Account", account)<br>
     **/
    public String createObject(String sObjectName, Object serializableObject) throws ConnectionException {
        return sObjectApi.createSObject(sObjectName, serializableObject);
    }

    /**
     * @param sObjectName Salesforce Object Name
     * @param serializableObject Any serializable object
     * @param id          Salesforce Object Id
     * @return String value of updated Object Id
     * @throws ConnectionException <br >Usage:<br >
     *                             Map&lt;String, Object&gt; accountDataMap = new HashMap&lt;String, String&gt;();<br>
     *                             accountDataMap.put("Name", "testName Surname");<br>
     *                             accountDataMap.put("email", "testName@example.com");<br>
     *                             webClient.updateObject("Account","ghdgjs8S", accountDataMap)<br>
     *                             <br ><br >Or with a pojo<br >
     *                             class Account {<br >
     *                                 private String name;<br >
     *                                 private String email;<br ><br >
     *
     *                                 public Account(String name, String email){<br >
     *                                      this.name = name;<br >
     *                                      this.email = email;<br >
     *                                 }<br >
     *                             }<br >
     *                             Account account = new Account("testName SurnameUpdated", "testName1@example.com");<br>
     *                             <p>
     *                             webClient.updateObject("Account","ghdgjs8S" account)<br>
     **/
    public String updateObject(String sObjectName, String id, Object serializableObject) throws ConnectionException {
        return sObjectApi.updateSObject(sObjectName, id, serializableObject);
    }

    /**
     * @param sObjectName Salesforce Object Name
     * @param serializableObject Any serializable object
     * @param externalIdFieldName   saleforce sObject external Id field Name
     * @return String value of updated Object Id
     * @throws ConnectionException <br >Usage:<br >
     *                             class Account {<br >
     *                                 private String name;<br >
     *                                 private String email;<br ><br >
     *
     *                                 public Account(String name, String email){<br >
     *                                      this.name = name;<br >
     *                                      this.email = email;<br >
     *                                 }<br >
     *                             }<br >
     *                             Account account = new Account("testName SurnameUpdated", "testName1@example.com");<br>
     *                             <p>
     *                             webClient.createOrUpdateObject("Account","Id" account)<br>
     **/
    public String createOrUpdateObject(String sObjectName, String externalIdFieldName, Object serializableObject) throws ConnectionException {
        return sObjectApi.createOrUpdateSObject(sObjectName, externalIdFieldName, serializableObject);
    }

    /**
     * @param id          Salesforce Object Id
     * @return String value of deleted Object Id
     * @throws ConnectionException <br >Usage:<br >
     *                             Map&lt;String, Object&gt; accountDataMap = new HashMap&lt;String, String&gt;();<br>
     *                             accountDataMap.put("Name", "testName Surname");<br>
     *                             accountDataMap.put("email", "testName@example.com");<br>
     *                             webClient.updateObject("Account", accountDataMap)<br>
     **/
    public String deleteObject(String id) throws ConnectionException {
        return sObjectApi.deleteSObject(id);
    }

    /**
     * @param sObjectName Salesforce Object Name
     * @param id          sObject salesforce Id
     * @return Map of Object Fields and values
     * @throws ConnectionException <br >Usage: <br >
     *                             webClient.retrieveSObject("Account", OjectId)
     **/
    public Map<String, Object> retrieveObject(String sObjectName, String id) throws ConnectionException {
        return sObjectApi.retrieveSObject(sObjectName, id);
    }


    /**
     * @param targetObjectName Salesforce Object Name
     * @return List of Maps representing each data row
     * @throws AsyncApiException   Saleforce Api AsyncApiException
     * @throws ConnectionException Saleforce Api ConnectionException
     * @throws IOException         Java IOException
     **/
    public List<Map<String, String>> exportDataFromTable(String targetObjectName) throws Exception {
        List<String> columns = sObjectApi.getDataColumns(targetObjectName);

        return exportDataFromTable(targetObjectName, columns, new HashMap<String, String>());
    }

    /**
     * @param targetObjectName Salesforce Object Name
     * @param columns          Columns to retrieveObject
     * @return List of Maps representing each data row
     * @throws AsyncApiException   Saleforce Api AsyncApiException
     * @throws ConnectionException Saleforce Api ConnectionException
     * @throws IOException         Java IOException
     *                             <p>
     *                             <br >Usage:<br >
     *                             List&lt;String&gt; columns = new ArrayList();<br>
     *                             columns.add("Id");<br>
     *                             webClient.exportDataFromTable("Account", columns);<br >
     **/
    public List<Map<String, String>> exportDataFromTable(String targetObjectName, List<String> columns) throws Exception {
        return exportDataFromTable(targetObjectName, columns, new HashMap<String, String>());
    }

    /**
     * @param targetObjectName Salesforce Object Name
     * @param filters          - Map of filters
     * @return List of Maps representing each data row
     * @throws AsyncApiException   Saleforce Api AsyncApiException
     * @throws ConnectionException Saleforce Api ConnectionException
     * @throws IOException         Java IOException
     **/
    public List<Map<String, String>> exportDataFromTable(String targetObjectName, Map<String, String> filters) throws Exception {
        List<String> columns = sObjectApi.getDataColumns(targetObjectName);

        return exportDataFromTable(targetObjectName, columns, filters);
    }

    /**
     * @param targetObjectName Salesforce Object Name
     * @param columns          Columns to retrieveObject
     * @param filters          - Map of filters
     * @return List of Maps representing each data row
     * @throws AsyncApiException   Saleforce Api AsyncApiException
     * @throws ConnectionException Saleforce Api ConnectionException
     * @throws IOException         Java IOException
     *                             <p>
     *                             <br >Usage:<br >
     *                             List&lt;String&gt; columns = new ArrayList();<br>
     *                             columns.add("Id");<br>
     *                             Map&lt;String,String&gt; filters = new HashMap&lt;String,String&gt;();<br>
     *                             filters.put("id", "abc123");<br>
     *                             webClient.exportDataFromTable("Account", columns);<br >
     **/
    public List<Map<String, Object>> exportDataFromTable(String targetObjectName, List<String> columns, Map<String, String> filters) throws Exception {
        String QUERY_TEMPLATE = "SELECT %s FROM " + targetObjectName;
        List<String> filterList = new ArrayList<>();

        for (Map.Entry entry : filters.entrySet()) {
            filterList.add(String.format("%s='%s'", entry.getKey(), entry.getValue()));
        }
        QUERY_TEMPLATE += filterList.size() == 0 ? "" : " WHERE " + StringUtils.join(filterList, ' AND ');

        String query = URLEncoder.encode(String.format(QUERY_TEMPLATE, StringUtils.join(columns, ',')), "UTF-8");
        try {
            List<Map<String, Object>> result = sObjectApi.executeSoqlQuery(query)
            return result == null ? result : result.sort();
        }catch (Exception e){
            throw new SalesforceApiOperationException(e.getMessage())
        }
    }

}
