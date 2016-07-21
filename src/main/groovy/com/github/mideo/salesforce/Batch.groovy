package com.github.mideo.salesforce;

import com.sforce.async.AsyncApiException;
import com.sforce.async.BatchInfo
import com.sforce.async.BulkConnection;
import com.sforce.async.JobInfo;


class Batch {

    BatchInfo batchInfo;
    JobInfo jobInfo;
    InputStream inputStream;
    BulkConnection bulkConnection;

    Batch addJob(JobInfo jobInfo) {
        batchInfo = new BatchInfo();
        this.jobInfo = jobInfo;
        return this;
    }

    Batch withInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        return this;
    }


    Batch createStream() throws AsyncApiException {
        batchInfo = bulkConnection
                .createBatchFromStream(jobInfo, inputStream);
        return this;
    }

    BatchInfo createBatch() throws AsyncApiException {
        batchInfo = bulkConnection
                .createBatchFromStream(jobInfo, inputStream);
        return batchInfo;
    }

    PublishResult finaliseJob() throws AsyncApiException {

        jobInfo = bulkConnection
                .closeJob(jobInfo.getId());
        return new PublishResult(
                batchInfo:batchInfo,
                jobInfo:jobInfo
        );
    }

    public String getBatchStatus(String jobId, String batchId) throws AsyncApiException {
        batchInfo = bulkConnection
                .getBatchInfo(jobId, batchId);
        return batchInfo
                .getState()
                .name();
    }
}

