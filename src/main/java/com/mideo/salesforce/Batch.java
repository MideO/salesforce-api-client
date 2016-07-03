package com.mideo.salesforce;

import com.sforce.async.AsyncApiException;
import com.sforce.async.BatchInfo;
import com.sforce.async.JobInfo;

import java.io.InputStream;


class Batch {

    private BatchInfo batchInfo;
    private JobInfo jobInfo;
    private InputStream csvInputStream;
    private SalesforceConnectionClient salesforceConnectionClient;
    private PublishResult publishResult;


    Batch(){
        batchInfo = new BatchInfo();
    }

    Batch addJob(JobInfo jobInfo) {
        this.jobInfo = jobInfo;
        return this;
    }

    Batch withSalesforceClient(SalesforceConnectionClient salesforceConnectionClient){
        this.salesforceConnectionClient = salesforceConnectionClient;
        return this;
    }

    Batch withCsvInputStream(InputStream csvInputStream) {
        this.csvInputStream = csvInputStream;
        return this;
    }


    Batch createStream() throws AsyncApiException {
        batchInfo = salesforceConnectionClient.getSalesForceWebServiceBulkConnection().createBatchFromStream(jobInfo, csvInputStream);
        return this;
    }

    PublishResult finaliseJob() throws AsyncApiException {

        jobInfo = salesforceConnectionClient
                .getSalesForceWebServiceBulkConnection()
                .closeJob(jobInfo.getId());
        publishResult = new PublishResult(batchInfo, jobInfo);
        return publishResult;
    }
}

