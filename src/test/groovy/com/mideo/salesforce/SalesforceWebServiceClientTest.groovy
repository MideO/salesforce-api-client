package com.mideo.salesforce

import com.sforce.async.BatchInfo
import com.sforce.async.BulkConnection
import com.sforce.async.JobInfo
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
            mockJobInfo.getId() >> '1234'
            mockJobInfo.getState() >> 'Closed'
            mockBulkConnection.createJob(_) >> mockJobInfo
            mockConnectionClient.getSalesForceWebServiceBulkConnection() >> mockBulkConnection
            mockBulkConnection.closeJob('1234') >> mockJobInfo
            mockBulkConnection.createBatchFromStream(mockJobInfo, inputStream) >> mockBatchInfo

            String result = webServiceClient.publishCsvToTable(inputStream, 'AccountTable')

        then:
            1 * mockBulkConnection.closeJob('1234')

    }
}
