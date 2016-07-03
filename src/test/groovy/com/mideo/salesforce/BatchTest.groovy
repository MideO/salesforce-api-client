package com.mideo.salesforce

import com.sforce.async.BatchInfo
import com.sforce.async.BatchStateEnum
import com.sforce.async.BulkConnection
import com.sforce.async.ContentType
import com.sforce.async.JobInfo
import com.sforce.async.JobStateEnum
import com.sforce.async.OperationEnum
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
            batch.withCsvInputStream(inputStream)
        then:
            assert batch.csvInputStream == inputStream

    }

    def "Should Create BatchInfo"() {
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
                    .withCsvInputStream(inputStream)
                    .createStream()
        then:
            assert resultBatch.batchInfo == mockBatchInfo
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
