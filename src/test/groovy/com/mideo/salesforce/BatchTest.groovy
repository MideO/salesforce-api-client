package com.mideo.salesforce

import com.sforce.async.BatchInfo
import com.sforce.async.BatchStateEnum
import com.sforce.async.BulkConnection
import com.sforce.async.JobInfo
import com.sforce.async.JobStateEnum
import spock.lang.Specification


class BatchTest extends Specification {
    Batch batch
    JobInfo jobInfo
    InputStream inputStream



    def "Should Add Job"() {
        given:
            batch = new Batch()
            jobInfo = new JobInfo()
            inputStream = new ByteArrayInputStream('abcd'.getBytes());
        when:
            batch.addJob(jobInfo)
        then:
            assert batch.jobInfo == jobInfo
            assert batch.batchInfo != null
    }

    def "Should Add Input Stream"() {
        given:
            batch = new Batch()
            inputStream = new ByteArrayInputStream('abcd'.getBytes());
        when:
            batch.withInputStream(inputStream)
        then:
            assert batch.inputStream == inputStream

    }

    def "Should Create BatchInfo from CreateStream"() {
        given:

            inputStream = new ByteArrayInputStream('abcd'.getBytes());
            SalesforceConnectionClient mockConnectionClient = Mock(SalesforceConnectionClient)
            BulkConnection mockBulkConnection = Mock(BulkConnection)
            BatchInfo mockBatchInfo = Mock(BatchInfo)
            batch = new Batch(salesforceConnectionClient:mockConnectionClient)
        when:
            mockConnectionClient.getSalesForceWebServiceBulkConnection() >> mockBulkConnection
            mockBulkConnection.createBatchFromStream(jobInfo, inputStream) >> mockBatchInfo

            Batch resultBatch = batch.addJob(jobInfo)
                    .withInputStream(inputStream)
                    .createStream()
        then:
            assert resultBatch.batchInfo == mockBatchInfo
    }

    def "Should Create BatchInfo from CreateBatchInfo"() {
        given:
            inputStream = new ByteArrayInputStream('abcd'.getBytes());
            SalesforceConnectionClient mockConnectionClient = Mock(SalesforceConnectionClient)
            BulkConnection mockBulkConnection = Mock(BulkConnection)
            BatchInfo mockBatchInfo = Mock(BatchInfo)
            batch = new Batch(salesforceConnectionClient:mockConnectionClient)
        when:
            mockConnectionClient.getSalesForceWebServiceBulkConnection() >> mockBulkConnection
            mockBulkConnection.createBatchFromStream(jobInfo, inputStream) >> mockBatchInfo
            batch = new Batch(salesforceConnectionClient:mockConnectionClient)

            BatchInfo batchInfo = batch.addJob(jobInfo)
                .withInputStream(inputStream)
                .createBatch()
        then:
            assert batchInfo == mockBatchInfo
    }

    def "Should finalise Job"() {
        given:
            SalesforceConnectionClient mockConnectionClient = Mock(SalesforceConnectionClient);
            BulkConnection mockBulkConnection = Mock(BulkConnection);
            JobInfo mockJobInfo = Mock(JobInfo);
            BatchInfo mockBatchInfo = Mock(BatchInfo);
            InputStream inputStream = new ByteArrayInputStream('abcd'.getBytes());
            batch = new Batch(salesforceConnectionClient:mockConnectionClient);

        when:
            mockJobInfo.getId() >> '1234';
            mockJobInfo.getState() >> JobStateEnum.Closed;
            mockJobInfo.getObject() >> 'AccountTable';
            mockBatchInfo.getState() >> BatchStateEnum.Failed;
            mockBulkConnection.closeJob(mockJobInfo.getId()) >> mockJobInfo;
            mockBulkConnection.createBatchFromStream(mockJobInfo, inputStream) >> mockBatchInfo;
            mockConnectionClient.getSalesForceWebServiceBulkConnection() >> mockBulkConnection;
            PublishResult publishResult = batch
                    .addJob(mockJobInfo)
                    .finaliseJob();

        then:
            assert !publishResult.isPublished();


    }


    def "Should return get batch status"() {
        given:
            SalesforceConnectionClient mockConnectionClient = Mock(SalesforceConnectionClient);
            BulkConnection mockBulkConnection = Mock(BulkConnection);
            BatchInfo mockBatchInfo = Mock(BatchInfo);
            batch = new Batch(salesforceConnectionClient:mockConnectionClient)

        when:
            mockBatchInfo.getState() >> BatchStateEnum.Failed;
            mockConnectionClient.getSalesForceWebServiceBulkConnection() >> mockBulkConnection;
            mockBulkConnection.getBatchInfo(_,_) >> mockBatchInfo;
        then:
            assert batch.getBatchStatus("abc","123") == "Failed";

    }
}
