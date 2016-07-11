package com.mideo.salesforce;


import com.sforce.async.*;
import com.sforce.ws.ConnectionException;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SalesforceWebServiceClient {


    private SalesforceConnectionClient salesforceConnectionClient;
    private Job job;
    private Batch batch;
    private DataFetcher dataFetcher;
    private SObjectApi SObjectApi;

    /**
     *  @param salesforceConnectionClient Initiated Salesforce Connection client
     *
     *  <br >Usage:<br >
     *     SalesforceConfig config = new SalesforceConfig("http://a.com","bghghg","67667","foo@bar.com","test1234","hghsd");<br >
     *     HttpRequestSpecificationBuilder httpRequestSpecificationBuilder = new HttpRequestSpecificationBuilder();<br >
     *     SalesforceConnectionClient connectionClient = new SalesforceConnectionClient(config, httpRequestSpecificationBuilder);<br >
     *     SalesforceWebServiceClient webClient = new SalesforceWebServiceClient(connectionClient);<br >
     *
     */
    public SalesforceWebServiceClient(SalesforceConnectionClient salesforceConnectionClient) {
        this.salesforceConnectionClient = salesforceConnectionClient;
        job = new Job();
        batch = new Batch();
        dataFetcher = new DataFetcher();
        SObjectApi = new SObjectApi();
    }

    /**
     *
     * @param csvInputStream RFC4180 [double qouted CSV, comma seperated, row ended with `\r\n`] Input String from String or file
     * @param targetObjectName Salesforce Object Name
     *
     * @return PublishResult publish result object
     *
     * @throws AsyncApiException Saleforce Api AsyncApiException
     **/
    public PublishResult publishCsvToTable(InputStream csvInputStream, String targetObjectName) throws AsyncApiException {
        JobInfo jobInfo = job.withSalesforceClient(salesforceConnectionClient)
                .newJobInfo(targetObjectName)
                .toInsert(ContentType.CSV)
                .create();

        return batch.withSalesforceClient(salesforceConnectionClient)
                .addJob(jobInfo)
                .withInputStream(csvInputStream)
                .createStream()
                .finaliseJob();

    }

    /**
     *
     * @param jobId Salesoforce Job Id
     * @param batchId Saleforce Batch Id
     *
     * @return String value of Status of Published Batch
     *
     * @throws AsyncApiException Saleforce Api AsyncApiException
     **/
    public String getPublishedDataStatus(String jobId, String batchId) throws AsyncApiException {
        BatchInfo batchInfo = salesforceConnectionClient
                .getSalesForceWebServiceBulkConnection()
                .getBatchInfo(jobId, batchId);
        return String.valueOf(batchInfo.getState());
    }


    /**
     *
     * @param targetObjectName Salesforce Object Name
     *
     * @return List of Maps representing each data row
     *
     * @throws AsyncApiException Saleforce Api AsyncApiException
     * @throws ConnectionException Saleforce Api ConnectionException
     * @throws IOException Java IOException
     **/
    public List<Map<String, String>> exportDataFromTable(String targetObjectName) throws AsyncApiException, ConnectionException, IOException {
        List<String> columns = SObjectApi.withSalesforceClient(salesforceConnectionClient)
                .getDataColumns(targetObjectName);

        return exportDataFromTable(targetObjectName, columns, new HashMap<>());
    }

    /**
     *
     * @param targetObjectName Salesforce Object Name
     * @param columns Columns to retrieve
     *
     * @return List of Maps representing each data row
     *
     * @throws AsyncApiException Saleforce Api AsyncApiException
     * @throws ConnectionException Saleforce Api ConnectionException
     * @throws IOException Java IOException
     *
     *   <br >Usage:<br >
     *     List&lt;String&gt; columns = new ArrayList();<br>
     *     columns.add("Id");<br>
     *     webClient.exportDataFromTable("Account", columns);<br >
     **/
    public List<Map<String, String>> exportDataFromTable(String targetObjectName, List<String> columns) throws AsyncApiException, ConnectionException, IOException {
        return exportDataFromTable(targetObjectName, columns, new HashMap<>());
    }

    /**
     *
     * @param targetObjectName Salesforce Object Name
     * @param filters - Map of filters
     *
     * @return List of Maps representing each data row
     *
     * @throws AsyncApiException Saleforce Api AsyncApiException
     * @throws ConnectionException Saleforce Api ConnectionException
     * @throws IOException Java IOException
     **/
    public List<Map<String, String>> exportDataFromTable(String targetObjectName, Map<String, String> filters) throws AsyncApiException, ConnectionException, IOException {
        List<String> columns = SObjectApi.withSalesforceClient(salesforceConnectionClient)
                .getDataColumns(targetObjectName);

        return exportDataFromTable(targetObjectName, columns, filters);
    }

    /**
     *
     * @param targetObjectName Salesforce Object Name
     * @param columns Columns to retrieve
     * @param filters - Map of filters
     * @return List of Maps representing each data row
     *
     * @throws AsyncApiException Saleforce Api AsyncApiException
     * @throws ConnectionException Saleforce Api ConnectionException
     * @throws IOException Java IOException
     *
     *   <br >Usage:<br >
     *     List&lt;String&gt; columns = new ArrayList();<br>
     *     columns.add("Id");<br>
     *     Map&lt;String,String&gt; filters = new HashMap&lt;String,String&gt;();<br>
     *     filters.put("id", "abc123");<br>
     *     webClient.exportDataFromTable("Account", columns);<br >
     **/
    public List<Map<String, String>> exportDataFromTable(String targetObjectName, List<String> columns, Map<String, String> filters) throws AsyncApiException, ConnectionException, IOException, NullPointerException {
        String QUERY_TEMPLATE = "SELECT %s FROM "+targetObjectName;
        List<String> filterList = new ArrayList<>();

        for (Map.Entry entry: filters.entrySet()){
            filterList.add(String.format("%s=\"%s\"", entry.getKey(), entry.getValue()));
        }
        QUERY_TEMPLATE += StringUtils.join(filterList, ',');

        String query = String.format(QUERY_TEMPLATE, StringUtils.join(columns,','));
        ByteArrayInputStream soqlInputStream = new ByteArrayInputStream(query.getBytes());



        JobInfo jobInfo = job.withSalesforceClient(salesforceConnectionClient)
                .withParallelConcurrencyMode()
                .newJobInfo(targetObjectName)
                .toQuery(ContentType.CSV)
                .setOperation(OperationEnum.query)
                .create();


        BatchInfo batchInfo = batch.withSalesforceClient(salesforceConnectionClient)
                .addJob(jobInfo)
                .withInputStream(soqlInputStream)
                .createBatch();


        return dataFetcher.withSalesforceClient(salesforceConnectionClient)
                .fetchData(jobInfo.getId(), batchInfo.getId());

    }



    /**
     *
     * @param sObjectName Salesforce Object Name
     * @param data key value pair for each data column to be populated
     *
     * @return String value of created Object Id
     *
     * @throws ConnectionException
     *
     *   <br >Usage:<br >
     *     Map&lt;String, Object&gt; accountDataMap = new HashMap&lt;String, String&gt;();<br>
     *     accountDataMap.put("Name", "testName Surname");<br>
     *     accountDataMap.put("email", "testName@example.com");<br><br>
     *
     *     webClient.createObject("Account", accountDataMap)<br>
     **/
    public String createObject(String sObjectName, Map<String, Object> data) throws ConnectionException {
        return SObjectApi.withSalesforceClient(salesforceConnectionClient)
                .createSObject(sObjectName, data);
    }

    /**
     *
     * @param sObjectName Salesforce Object Name
     * @param data key value pair for each data column to be populated
     * @param id Salesforce Object Id
     *
     * @return String value of created Object Id
     *
     * @throws ConnectionException
     *
     *   <br >Usage:<br >
     *     Map&lt;String, Object&gt; accountDataMap = new HashMap&lt;String, String&gt;();<br>
     *     accountDataMap.put("Name", "testName Surname");<br>
     *     accountDataMap.put("email", "testName@example.com");<br>
     *     webClient.updateObject("Account", accountDataMap)<br>
     **/
    public String updateObject(String sObjectName, String id, Map<String, Object> data) throws ConnectionException {
        return SObjectApi.withSalesforceClient(salesforceConnectionClient)
                .updateSObject(sObjectName, id, data);
    }


    /**
     *
     * @param sObjectName Salesforce Object Name
     * @param id sObject salesforce Id
     *
     * @return Map of Object Fields and values
     *
     * @throws ConnectionException
     *
     *   <br >Usage: <br >
     *     webClient.retrieveSObject("Account", OjectId)
     **/
    public Map<String, Object> retrieve(String sObjectName, String id) throws ConnectionException {
        return SObjectApi.withSalesforceClient(salesforceConnectionClient)
                .retrieveSObject(sObjectName, id);
    }

}
