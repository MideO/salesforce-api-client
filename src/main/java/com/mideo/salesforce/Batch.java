package com.mideo.salesforce;

import com.sforce.async.AsyncApiException;
import com.sforce.async.BatchInfo;
import com.sforce.async.JobInfo;

import java.io.InputStream;


class Batch {

    private BatchInfo batchInfo;
    private JobInfo job;
    private InputStream csvInputStream;
    private SalesforceConnectionClient salesforceConnectionClient;


    Batch(){
        batchInfo = new BatchInfo();
    }

    Batch addJob(JobInfo job) {
        this.job = job;
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
        batchInfo = salesforceConnectionClient.getSalesForceWebServiceBulkConnection().createBatchFromStream(job, csvInputStream);
        return this;
    }

    String finaliseJob() throws AsyncApiException {
        salesforceConnectionClient
                .getSalesForceWebServiceBulkConnection()
                .closeJob(job.getId());
        return job.getState().toString();
    }
}

