package com.mideo.salesforce;

import com.sforce.async.AsyncApiException;
import com.sforce.async.BatchInfo;
import com.sforce.async.JobInfo;

import java.io.InputStream;


class Batch {

    private BatchInfo batchInfo;
    private JobInfo job;
    private InputStream csvInputStream;


    Batch(){
        batchInfo = new BatchInfo();
    }

    Batch addJob(JobInfo job) {
        this.job = job;
        return this;
    }

    Batch withCsvInputStream(InputStream csvInputStream) {
        this.csvInputStream = csvInputStream;
        return this;
    }


    BatchInfo create(SalesforceConnectionClient salesforceConnectionClient) throws AsyncApiException {
        batchInfo = salesforceConnectionClient.getSalesForceWebServiceBulkConnection().createBatchFromStream(job, csvInputStream);
        return batchInfo;
    }
}

