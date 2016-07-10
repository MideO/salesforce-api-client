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
            batch = new Batch()
            inputStream = new ByteArrayInputStream('abcd'.getBytes());
            SalesforceConnectionClient mockConnectionClient = Mock(SalesforceConnectionClient)
            BulkConnection mockBulkConnection = Mock(BulkConnection)
            BatchInfo mockBatchInfo = Mock(BatchInfo)
        when:
            mockConnectionClient.getSalesForceWebServiceBulkConnection() >> mockBulkConnection
            mockBulkConnection.createBatchFromStream(jobInfo, inputStream) >> mockBatchInfo

            Batch resultBatch = batch.addJob(jobInfo)
                    .withSalesforceClient(mockConnectionClient)
                    .withInputStream(inputStream)
                    .createStream()
        then:
            assert resultBatch.batchInfo == mockBatchInfo
    }

    def "Should Create BatchInfo from CreateBatchInfo"() {
        given:
        batch = new Batch()
        inputStream = new ByteArrayInputStream('abcd'.getBytes());
        SalesforceConnectionClient mockConnectionClient = Mock(SalesforceConnectionClient)
        BulkConnection mockBulkConnection = Mock(BulkConnection)
        BatchInfo mockBatchInfo = Mock(BatchInfo)
        when:
        mockConnectionClient.getSalesForceWebServiceBulkConnection() >> mockBulkConnection
        mockBulkConnection.createBatchFromStream(jobInfo, inputStream) >> mockBatchInfo

        BatchInfo batchInfo = batch.addJob(jobInfo)
                .withSalesforceClient(mockConnectionClient)
                .withInputStream(inputStream)
                .createBatch()
        then:
        assert batchInfo == mockBatchInfo
    }

    def "Should finalise Job"() {
        given:
            SalesforceConnectionClient mockConnectionClient = Mock(SalesforceConnectionClient)
            BulkConnection mockBulkConnection = Mock(BulkConnection)
            JobInfo mockJobInfo = Mock(JobInfo)
            BatchInfo mockBatchInfo = Mock(BatchInfo)
            InputStream inputStream = new ByteArrayInputStream('abcd'.getBytes())
            Batch batch  = new Batch().withSalesforceClient(mockConnectionClient).addJob(mockJobInfo)

        when:
            mockJobInfo.getId() >> '1234';
            mockJobInfo.getState() >> JobStateEnum.Closed
            mockJobInfo.getObject() >> 'AccountTable'
            mockBatchInfo.getState() >> BatchStateEnum.Failed
            mockBulkConnection.closeJob(mockJobInfo.getId()) >> mockJobInfo
            mockBulkConnection.createBatchFromStream(mockJobInfo, inputStream) >> mockBatchInfo
            mockConnectionClient.getSalesForceWebServiceBulkConnection() >> mockBulkConnection
            PublishResult publishResult = batch.finaliseJob()

        then:
            assert !publishResult.isPublished()


    }
}
