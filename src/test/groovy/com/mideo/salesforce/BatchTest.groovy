package com.mideo.salesforce

import com.sforce.async.BatchInfo
import com.sforce.async.BulkConnection
import com.sforce.async.ContentType
import com.sforce.async.JobInfo
import com.sforce.async.OperationEnum
import spock.lang.Specification


class BatchTest extends Specification {
    Batch batch
    JobInfo jobInfo
    InputStream inputStream



    def "Should Add Job"() {
        given:
            batch = new Batch()
            inputStream = new ByteArrayInputStream("abcd".getBytes());
        when:
            batch.addJob(jobInfo)
        then:
            assert batch.job == jobInfo
    }

    def "Should Add Input Stream"() {
        given:
            batch = new Batch()
            inputStream = new ByteArrayInputStream("abcd".getBytes());
        when:
            batch.withCsvInputStream(inputStream)
        then:
            assert batch.csvInputStream == inputStream

    }

    def "Should Create BatchInfo"() {
        given:
            batch = new Batch()
            inputStream = new ByteArrayInputStream("abcd".getBytes());
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
            batch = new Batch()
            inputStream = new ByteArrayInputStream("abcd".getBytes());
            SalesforceConnectionClient mockConnectionClient = Mock(SalesforceConnectionClient)
            BulkConnection mockBulkConnection = Mock(BulkConnection)
            JobInfo mockJobInfo = Mock(JobInfo)
            BatchInfo mockBatchInfo = Mock(BatchInfo)

        when:
            mockConnectionClient.getSalesForceWebServiceBulkConnection() >> mockBulkConnection
            mockBulkConnection.createJob(_) >> mockJobInfo
            mockConnectionClient.getSalesForceWebServiceBulkConnection().closeJob(_) >> mockJobInfo
            mockBulkConnection.createBatchFromStream(jobInfo, inputStream) >> mockBatchInfo

            JobInfo jobInfo = new Job()
                    .newJob("jobby")
                    .withSalesforceClient(mockConnectionClient)
                    .setOperation(OperationEnum.insert)
                    .setContentType(ContentType.CSV)
                    .create()

        JobInfo resultJobInfo = batch
                .withSalesforceClient(mockConnectionClient)
                .addJob(jobInfo)
                .withCsvInputStream(inputStream)
                .createStream()
                .finaliseJob()

        then:
            assert jobInfo == resultJobInfo


    }
}
