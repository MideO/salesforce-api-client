package com.mideo.salesforce;

import com.sforce.async.AsyncApiException;
import com.sforce.async.BatchInfo;
import com.sforce.async.JobInfo;

import java.io.InputStream;


class Batch {

    BatchInfo batchInfo;
    private JobInfo jobInfo;
    private InputStream inputStream;
    private SalesforceConnectionClient salesforceConnectionClient;


    Batch(){
        batchInfo = new BatchInfo();
    }

    Batch addJob(JobInfo jobInfo) {
        this.jobInfo = jobInfo;
        return this;
    }

    Batch withSalesforceClient(SalesforceConnectionClient salesforceConnectionClient){
        this.salesforceConnectionClient = salesforceConnectionClient;
        batchInfo = new BatchInfo();
        return this;
    }

    Batch withInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        return this;
    }


    Batch createStream() throws AsyncApiException {
        batchInfo = salesforceConnectionClient.getSalesForceWebServiceBulkConnection().createBatchFromStream(jobInfo, inputStream);
        return this;
    }

    BatchInfo createBatch() throws AsyncApiException {
        batchInfo = salesforceConnectionClient.getSalesForceWebServiceBulkConnection().createBatchFromStream(jobInfo, inputStream);
        return batchInfo;
    }

    PublishResult finaliseJob() throws AsyncApiException {

        jobInfo = salesforceConnectionClient
                .getSalesForceWebServiceBulkConnection()
                .closeJob(jobInfo.getId());
        return new PublishResult(batchInfo, jobInfo);
    }

    public String getBatchStatus(String jobId, String batchId) throws AsyncApiException {
        batchInfo = salesforceConnectionClient
                .getSalesForceWebServiceBulkConnection()
                .getBatchInfo(jobId, batchId);
        return batchInfo.getState().name();
    }
}

