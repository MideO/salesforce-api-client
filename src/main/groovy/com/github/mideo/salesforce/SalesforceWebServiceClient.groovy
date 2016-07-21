package com.github.mideo.salesforce


import com.sforce.async.*
import com.sforce.soap.apex.ExecuteAnonymousResult

import com.sforce.ws.ConnectionException;
import org.apache.commons.lang3.StringUtils;


public class SalesforceWebServiceClient {


    private SalesforceConnectionClient salesforceConnectionClient;
    long publishStatusCheckTimeout = 30000;
    Job job;
    Batch batch;
    DataFetcher dataFetcher;
    SObjectApi sObjectApi;

    /**
     * @param salesforceConnectionClient Initiated Salesforce Connection client
     *                                   <p>
     *                                   <br >Usage:<br >
     *                                   SalesforceConfig config = new SalesforceConfig("abc").clientId("wewew").clientSecret("dfdfd").userName("sdsds").password("sdsds").userToken("sdssd");<br >
     *                                   HttpRequest httpRequestSpecificationBuilder = new HttpRequest();<br >
     *                                   SalesforceConnectionClient connectionClient = new SalesforceConnectionClient(config, httpRequestSpecificationBuilder);<br >
     *                                   SalesforceWebServiceClient webClient = new SalesforceWebServiceClient(connectionClient);<br >
     */
    public SalesforceWebServiceClient(SalesforceConnectionClient salesforceConnectionClient) {
        this.salesforceConnectionClient = salesforceConnectionClient;

        job = new Job(bulkConnection: salesforceConnectionClient.getSalesForceWebServiceBulkConnection());
        batch = new Batch(bulkConnection: salesforceConnectionClient.getSalesForceWebServiceBulkConnection());
        dataFetcher = new DataFetcher(bulkConnection: salesforceConnectionClient.getSalesForceWebServiceBulkConnection());

        sObjectApi = new SObjectApi(
                partnerConnection: salesforceConnectionClient.getSalesForceWebServicePartnerConnection(),
                soapConnection: salesforceConnectionClient.getSalesforceSoapConnection(),
                session: salesforceConnectionClient.getSalesforceSession(),
                requestSpecification: salesforceConnectionClient.requestSpecification
        )


    }

    /**
     * @param csvInputStream   RFC4180 [double qouted CSV, comma seperated, row ended with `\r\n`] Input String from String or file
     * @param targetObjectName Salesforce Object Name
     * @return PublishResult publish result object
     * @throws AsyncApiException Saleforce Api AsyncApiException
     **/
    public PublishResult publishCsvToTable(InputStream csvInputStream, String targetObjectName) throws AsyncApiException {
        JobInfo jobInfo = job.newJobInfo(targetObjectName)
                    .toInsert(ContentType.CSV)
                .create();

        return batch.addJob(jobInfo)
                .withInputStream(csvInputStream)
                .createStream()
                .finaliseJob();

    }

    /**
     * @param publishStatusCheckTimeoutInMilliSeconds - Polling timeout in milliseconds
     * @return SalesforceWebServiceClient
     */
    public SalesforceWebServiceClient setPublishStatusCheckTimeout(long publishStatusCheckTimeoutInMilliSeconds) {
        this.publishStatusCheckTimeout = publishStatusCheckTimeoutInMilliSeconds;
        return this;
    }


    /**
     * @param apexCode - apex code to execute
     * @return
     */
    public ExecuteAnonymousResult executeApexBlock(String apexCode) {
        return sObjectApi.executeApexBlock(apexCode);
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
    public String getPublishedDataStatus(String jobId, String batchId) throws AsyncApiException {
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
    public List<Map<String, String>> exportDataFromTable(String targetObjectName, List<String> columns, Map<String, String> filters) throws Exception {
        String QUERY_TEMPLATE = "SELECT %s FROM " + targetObjectName;
        List<String> filterList = new ArrayList<>();

        for (Map.Entry entry : filters.entrySet()) {
            filterList.add(String.format("%s='%s'", entry.getKey(), entry.getValue()));
        }
        QUERY_TEMPLATE += filterList.size() == 0 ? "" : " WHERE " + StringUtils.join(filterList, ',');

        String query = String.format(QUERY_TEMPLATE, StringUtils.join(columns, ','));
        ByteArrayInputStream soqlInputStream = new ByteArrayInputStream(query.getBytes());


        JobInfo jobInfo = job.newJobInfo(targetObjectName)
                .withParallelConcurrencyMode()
                .toQuery(ContentType.CSV)
                .setOperation(OperationEnum.query)
                .create();


        BatchInfo batchInfo = batch.addJob(jobInfo)
                .withInputStream(soqlInputStream)
                .createBatch();

        long counter = 0;
        long sleepTime = 1000;

        try{
            while (counter <= publishStatusCheckTimeout) {
            if (getPublishedDataStatus(jobInfo.getId(), batchInfo.getId()).equals(BatchStateEnum.Completed.name())) {

                return dataFetcher.fetchData(jobInfo.getId(), batchInfo.getId());
            }
            if (getPublishedDataStatus(jobInfo.getId(), batchInfo.getId()).equals(BatchStateEnum.Failed.name())) {

                throw new SalesforceApiOperationException("Salesforce Bulk Api Operation Failed: \n" + batch.batchInfo);
            }
            counter += sleepTime;
            Thread.sleep(sleepTime);
        }}finally {
            publishStatusCheckTimeout = 30000;
        }

        throw new SalesforceApiOperationException("Salesforce Bulk Api Operation timedOut after "+counter+" Milliseconds \n" + batch.batchInfo);
    }
}
