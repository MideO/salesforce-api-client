package com.mideo.salesforce;


import com.sforce.async.*;

import java.io.InputStream;


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
                .newJob(targetObjectName)
                .setOperation(OperationEnum.insert)
                .setContentType(ContentType.CSV)
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
}
