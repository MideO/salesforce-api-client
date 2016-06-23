package com.mideo.salesforce

import com.sforce.async.BatchInfo
import com.sforce.async.BulkConnection
import com.sforce.async.JobInfo
import spock.lang.Specification


class BatchTest extends Specification {
    Batch batch
    JobInfo jobInfo
    InputStream inputStream


    void setup() {
        batch = new Batch()
        jobInfo = new JobInfo()
        inputStream = new ByteArrayInputStream("abcd".getBytes());
    }

    def "Should Add Job"() {
        when:
            batch.addJob(jobInfo)
        then:
            assert batch.job == jobInfo
    }

    def "Should Add Input Stream"() {
        when:
            batch.withCsvInputStream(inputStream)
        then:
            assert batch.csvInputStream == inputStream

    }

    def "Should Create BatchInfo"() {
        given:
            SalesforceConnectionClient mockConnectionClient = Mock(SalesforceConnectionClient)
            BulkConnection mockBulkConnection = Mock(BulkConnection)
            BatchInfo mockBatchInfo = Mock(BatchInfo)
        when:
            mockConnectionClient.getSalesForceWebServiceBulkConnection() >> mockBulkConnection
            mockBulkConnection.createBatchFromStream(jobInfo, inputStream) >> mockBatchInfo

            BatchInfo batchInfo = batch.addJob(jobInfo)
                    .withCsvInputStream(inputStream)
                    .create(mockConnectionClient)
        then:
            assert batchInfo == mockBatchInfo
    }
}
