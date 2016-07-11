package com.mideo.salesforce;


import com.sforce.async.*;
import com.sforce.ws.ConnectionException;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;


public class SalesforceWebServiceClient {

    private SalesforceConnectionClient salesforceConnectionClient;
    private Job job;
    private Batch batch;
    private DataFetcher dataFetcher;
    private ObjectDescriber objectDescriber;

    public SalesforceWebServiceClient(SalesforceConnectionClient salesforceConnectionClient) {
        this.salesforceConnectionClient = salesforceConnectionClient;
        job = new Job();
        batch = new Batch();
        dataFetcher = new DataFetcher();
        objectDescriber = new ObjectDescriber();
    }


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


    public String getPublishedDataStatus(String jobId, String batchId) throws AsyncApiException {
        BatchInfo batchInfo = salesforceConnectionClient
                .getSalesForceWebServiceBulkConnection()
                .getBatchInfo(jobId, batchId);
        return String.valueOf(batchInfo.getState());
    }

    public List<Map<String, String>> exportDataFromTable(String targetObjectName) throws AsyncApiException, ConnectionException, IOException {
        List<String> columns = objectDescriber.withSalesforceClient(salesforceConnectionClient)
                .getDataColumns(targetObjectName);
        return exportDataFromTable(targetObjectName, columns);
    }

    public List<Map<String, String>> exportDataFromTable(String targetObjectName, List<String> columns) throws AsyncApiException, ConnectionException, IOException {
        String QUERY_TEMPLATE = "SELECT %s FROM "+targetObjectName;

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
}
