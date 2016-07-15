package com.mideo.salesforce;

import com.sforce.async.BatchInfo;
import com.sforce.async.BatchStateEnum;
import com.sforce.async.JobInfo;
import com.sforce.async.JobStateEnum;

class PublishResult {
    BatchInfo batchInfo;
    JobInfo jobInfo;
    String targetObjectName

    public boolean isPublished() {
        return (batchInfo.getState() != null && batchInfo.getState() != BatchStateEnum.Failed && jobInfo.getState() == JobStateEnum.Closed);
    }
}