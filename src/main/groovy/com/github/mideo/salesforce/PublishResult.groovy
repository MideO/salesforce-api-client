package com.github.mideo.salesforce;

import com.sforce.async.BatchInfo;
import com.sforce.async.BatchStateEnum;
import com.sforce.async.JobInfo;
import com.sforce.async.JobStateEnum;

class PublishResult {
    BatchInfo batchInfo;
    JobInfo jobInfo;

    public boolean isPublished() {
        return (batchInfo.getState() != null && batchInfo.getState() != BatchStateEnum.Failed && jobInfo.numberRecordsFailed == 0 );
    }

}
