package com.mideo.salesforce

import com.sforce.async.BatchInfo
import com.sforce.async.BatchStateEnum
import com.sforce.async.JobInfo
import com.sforce.async.JobStateEnum
import spock.lang.Specification


class PublishResultTest extends Specification {
    PublishResult publishResult;
    BatchInfo batchInfo;
    JobInfo jobInfo;

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
    def "Should return Job Id"() {
        given:
            publishResult = new PublishResult(batchInfo, jobInfo);
        when:
            String jobId = publishResult.getJobId()
        then:
            assert jobId == 'job123'
    }

    def "Should return targetObjectName"() {
        given:
            publishResult = new PublishResult(batchInfo, jobInfo);

        when:
            String targetObjectName = publishResult.getTargetObjectName();

        then:
            assert targetObjectName == 'abc123'
    }

    def "Should return batchId"() {
        given:
            publishResult = new PublishResult(batchInfo, jobInfo);

        when:
            String batchId = publishResult.getBatchId();
        then:
            assert batchId == 'batch123'
    }

    def "Should be true If Job State is Closed and Batch is not Failed is Successful"() {
        given:
            publishResult = new PublishResult(batchInfo, jobInfo);
        when:
            boolean isPublished = publishResult.isPublished();
        then:
            assert isPublished
    }

    def "Should be False If Job State is not Closed and Batch is not Failed is Successful"() {
        given:
            jobInfo.state = JobStateEnum.Failed;
            publishResult = new PublishResult(batchInfo, jobInfo);
        when:
            boolean isPublished = publishResult.isPublished();
        then:
            assert !isPublished
    }
}
