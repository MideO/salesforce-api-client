package com.mideo.salesforce;


import com.sforce.async.*
import com.sforce.soap.apex.ExecuteAnonymousResult;
import com.sforce.ws.ConnectionException;
import org.apache.commons.lang3.StringUtils;


public class SalesforceWebServiceClient {


    private SalesforceConnectionClient salesforceConnectionClient;
    private Job job;
    private Batch batch;
    private DataFetcher dataFetcher;
    private SObjectApi SObjectApi;
    long publishStatusCheckTimeout = 30000;

    /**
     * @param salesforceConnectionClient Initiated Salesforce Connection client
     *                                   <p>
     *                                   <br >Usage:<br >
     *                                   SalesforceConfig config = new SalesforceConfig("abc").clientId("wewew").clientSecret("dfdfd").userName("sdsds").password("sdsds").userToken("sdssd");<br >
     *                                   HttpRequestSpecificationBuilder httpRequestSpecificationBuilder = new HttpRequestSpecificationBuilder();<br >
     *                                   SalesforceConnectionClient connectionClient = new SalesforceConnectionClient(config, httpRequestSpecificationBuilder);<br >
     *                                   SalesforceWebServiceClient webClient = new SalesforceWebServiceClient(connectionClient);<br >
     */
    public SalesforceWebServiceClient(SalesforceConnectionClient salesforceConnectionClient) {
        this.salesforceConnectionClient = salesforceConnectionClient;
        job = new Job(salesforceConnectionClient: salesforceConnectionClient);
        batch = new Batch(salesforceConnectionClient: salesforceConnectionClient);
        dataFetcher = new DataFetcher(salesforceConnectionClient: salesforceConnectionClient);
        SObjectApi = new SObjectApi(salesforceConnectionClient: salesforceConnectionClient);
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
        return SObjectApi.executeApexBlock(apexCode)



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
     * @param data        key value pair for each data column to be populated
     * @return String value of created Object Id
     * @throws ConnectionException <br >Usage:<br >
     *                             Map&lt;String, Object&gt; accountDataMap = new HashMap&lt;String, String&gt;();<br>
     *                             accountDataMap.put("Name", "testName Surname");<br>
     *                             accountDataMap.put("email", "testName@example.com");<br><br>
     *                             <p>
     *                             webClient.createObject("Account", accountDataMap)<br>
     **/
    public String createObject(String sObjectName, Map<String, Object> data) throws ConnectionException {
        return SObjectApi.createSObject(sObjectName, data);
    }

    /**
     * @param sObjectName Salesforce Object Name
     * @param data        key value pair for each data column to be populated
     * @param id          Salesforce Object Id
     * @return String value of updated Object Id
     * @throws ConnectionException <br >Usage:<br >
     *                             Map&lt;String, Object&gt; accountDataMap = new HashMap&lt;String, String&gt;();<br>
     *                             accountDataMap.put("Name", "testName Surname");<br>
     *                             accountDataMap.put("email", "testName@example.com");<br>
     *                             webClient.updateObject("Account", accountDataMap)<br>
     **/
    public String updateObject(String sObjectName, String id, Map<String, Object> data) throws ConnectionException {
        return SObjectApi.updateSObject(sObjectName, id, data);
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
        return SObjectApi.deleteSObject(id);
    }

    /**
     * @param sObjectName Salesforce Object Name
     * @param id          sObject salesforce Id
     * @return Map of Object Fields and values
     * @throws ConnectionException <br >Usage: <br >
     *                             webClient.retrieveSObject("Account", OjectId)
     **/
    public Map<String, Object> retrieveObject(String sObjectName, String id) throws ConnectionException {
        return SObjectApi.retrieveSObject(sObjectName, id);
    }


    /**
     * @param targetObjectName Salesforce Object Name
     * @return List of Maps representing each data row
     * @throws AsyncApiException   Saleforce Api AsyncApiException
     * @throws ConnectionException Saleforce Api ConnectionException
     * @throws IOException         Java IOException
     **/
    public List<Map<String, String>> exportDataFromTable(String targetObjectName) throws Exception {
        List<String> columns = SObjectApi.getDataColumns(targetObjectName);

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
        List<String> columns = SObjectApi.getDataColumns(targetObjectName);

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
