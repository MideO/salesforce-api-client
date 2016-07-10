package com.mideo.salesforce;


import com.sforce.async.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class SalesforceWebServiceClient {

    private SalesforceConnectionClient salesforceConnectionClient;
    private Job job;
    private Batch batch;

    public SalesforceWebServiceClient(SalesforceConnectionClient salesforceConnectionClient) {
        this.salesforceConnectionClient = salesforceConnectionClient;
        job = new Job();
        batch = new Batch();
    }


    public PublishResult publishCsvToTable(InputStream csvInputStream, String targetObjectName) throws AsyncApiException {
        JobInfo jobInfo = job.withSalesforceClient(salesforceConnectionClient)
                .newJobInfo(targetObjectName)
                .toInsert(ContentType.CSV)
                .create();

        return batch.withSalesforceClient(salesforceConnectionClient)
                .addJob(jobInfo)
                .withCsvInputStream(csvInputStream)
                .createStream()
                .finaliseJob();

    }


    public String getPublishedDataStatus(String jobId, String batchId) throws AsyncApiException {
        BatchInfo batchInfo = salesforceConnectionClient
                .getSalesForceWebServiceBulkConnection()
                .getBatchInfo(jobId, batchId);
        return String.valueOf(batchInfo.getState());
    }
    //TODO: Add test
    //TODO:  use bulk query: see https://developer.salesforce.com/docs/atlas.en-us.api_asynch.meta/api_asynch/asynch_api_using_bulk_query.htm
    public List<Map<String, Object>> ExportDataFromTable(String targetObjectName) throws AsyncApiException {
        JobInfo jobInfo = job.withSalesforceClient(salesforceConnectionClient)
                .withParallelConcurrentcyMode()
                .newJobInfo(targetObjectName)
                .toQuery(ContentType.CSV)
                .setOperation(OperationEnum.query)
                .create();


        return new ArrayList<>();
    }
}
