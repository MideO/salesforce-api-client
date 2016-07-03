package com.mideo.salesforce;

import com.sforce.async.BatchInfo;
import com.sforce.async.BatchStateEnum;
import com.sforce.async.JobInfo;
import com.sforce.async.JobStateEnum;

class PublishResult {

    private final String jobId;
    private final String targetObjectName;
    private final String batchId;
    private final boolean published;

    PublishResult(BatchInfo batchInfo, JobInfo jobInfo) {
        jobId = batchInfo.getJobId();
        targetObjectName = jobInfo.getObject();
        batchId = batchInfo.getId();
        published = (batchInfo.getState() != null && batchInfo.getState() != BatchStateEnum.Failed && jobInfo.getState() == JobStateEnum.Closed);
    }


    public String getJobId() {
        return jobId;
    }

    public String getTargetObjectName() {
        return targetObjectName;
    }

    public String getBatchId() {
        return batchId;
    }

    public boolean isPublished() {
        return published;
    }
}
