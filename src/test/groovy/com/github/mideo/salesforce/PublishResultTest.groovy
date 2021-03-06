package com.github.mideo.salesforce

import com.sforce.async.BatchInfo
import com.sforce.async.BatchStateEnum
import com.sforce.async.JobInfo
import com.sforce.async.JobStateEnum
import spock.lang.Specification


class PublishResultTest extends Specification {
    def publishResult;
    def batchInfo;
    def jobInfo;

    def setup(){
        batchInfo = new BatchInfo();
        batchInfo.id = 'batch123'
        batchInfo.state = BatchStateEnum.Completed
        jobInfo = new JobInfo();
        jobInfo.id = 'job123'
        jobInfo.object = 'abc123'
        jobInfo.state = JobStateEnum.Closed;
        batchInfo.setJobId(jobInfo.id)

    }

    def "Should be true If Job State is Closed and Batch is not Failed is Successful"() {

        given:
            jobInfo.numberRecordsFailed = 0;

        when:
            publishResult = new PublishResult(
                batchInfo:batchInfo,
                jobInfo:jobInfo);
        then:
            assert publishResult.isPublished();
    }

    def "Should be False If Job numberRecordsFailed is not 0and Batch is not Failed is Successful"() {
        given:
            jobInfo.numberRecordsFailed = 5;
        when:
            publishResult = new PublishResult(
                batchInfo:batchInfo,
                jobInfo:jobInfo);
        then:
            assert !publishResult.isPublished();
    }
}
