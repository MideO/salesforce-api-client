package com.mideo.salesforce

import com.sforce.async.BatchInfo
import com.sforce.async.BatchStateEnum
import com.sforce.async.BulkConnection
import com.sforce.async.JobInfo
import com.sforce.async.JobStateEnum
import spock.lang.Specification


class SalesforceWebServiceClientTest extends Specification {


    def "Should publish CSV to salesforce table"() {

        given:
            SalesforceConnectionClient mockConnectionClient = Mock(SalesforceConnectionClient)
            BulkConnection mockBulkConnection = Mock(BulkConnection)
            JobInfo mockJobInfo = Mock(JobInfo)
            BatchInfo mockBatchInfo = Mock(BatchInfo)
            InputStream inputStream = new ByteArrayInputStream('abcd'.getBytes())
            SalesforceWebServiceClient webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)

        when:
            mockJobInfo.getId() >> '1234';
            mockJobInfo.getState() >> JobStateEnum.Closed
            mockJobInfo.getObject() >> 'AccountTable'
            mockBatchInfo.getState() >> BatchStateEnum.Completed
            mockBulkConnection.createJob(_) >> mockJobInfo
            mockBulkConnection.closeJob(mockJobInfo.getId()) >> mockJobInfo
            mockConnectionClient.getSalesForceWebServiceBulkConnection() >> mockBulkConnection
            mockBulkConnection.createBatchFromStream(mockJobInfo, inputStream) >> mockBatchInfo

            PublishResult publishResult = webServiceClient.publishCsvToTable(inputStream, 'AccountTable')


        then:
            assert publishResult.isPublished();

    }
}
