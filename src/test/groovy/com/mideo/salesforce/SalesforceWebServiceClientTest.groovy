package com.mideo.salesforce

import com.sforce.async.BatchInfo
import com.sforce.async.BatchStateEnum
import com.sforce.async.BulkConnection
import com.sforce.async.JobInfo
import com.sforce.async.JobStateEnum
import com.sforce.async.QueryResultList
import com.sforce.soap.partner.DescribeSObjectResult
import com.sforce.soap.partner.Field
import com.sforce.soap.partner.PartnerConnection
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

    def "Should get Published Data Status"() {
        given:
            SalesforceConnectionClient mockConnectionClient = Mock(SalesforceConnectionClient)
            BulkConnection mockBulkConnection = Mock(BulkConnection)
            BatchInfo mockBatchInfo = Mock(BatchInfo)
            SalesforceWebServiceClient webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)

        when:
            mockBatchInfo.getJobId() >> '1234'
            mockBatchInfo.getId() >> '6789';
            mockBatchInfo.getState() >> BatchStateEnum.InProgress
            mockBulkConnection.getBatchInfo(mockBatchInfo.getJobId(), mockBatchInfo.getId()) >> mockBatchInfo
            mockConnectionClient.getSalesForceWebServiceBulkConnection() >> mockBulkConnection

        then:
            assert webServiceClient.getPublishedDataStatus(mockBatchInfo.getJobId(), mockBatchInfo.getId()) == 'InProgress'
    }

    def "Should Export Data From Table HashMap"() {
        given:
            SalesforceConnectionClient mockConnectionClient = Mock(SalesforceConnectionClient);
            BulkConnection mockBulkConnection = Mock(BulkConnection);

            JobInfo mockJobInfo = Mock(JobInfo)
            BatchInfo mockBatchInfo = Mock(BatchInfo)

            PartnerConnection mockPartnerConnection = Mock(PartnerConnection);
            DescribeSObjectResult mockDescribeSObjectResult = Mock(DescribeSObjectResult);
            Field mockField = Mock(Field);
            Field[] mockFields = [mockField];

            QueryResultList mockQueryResultList =  Mock(QueryResultList);
            SalesforceWebServiceClient webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)

        when:
            String resultId= 'z1x';
            String tableName = "ProductsTable"
            mockJobInfo.getId() >> '1234';
            mockBatchInfo.getId() >> '6789';

            mockConnectionClient.getSalesForceWebServicePartnerConnection() >> mockPartnerConnection;
            mockBulkConnection.getQueryResultList(_, _) >> mockQueryResultList;
            mockQueryResultList.getResult() >> [resultId];
            mockPartnerConnection.describeSObject(tableName) >> mockDescribeSObjectResult;
            mockDescribeSObjectResult.getFields() >> mockFields;
            mockField.getName() >> "fruit";


            mockConnectionClient.getSalesForceWebServiceBulkConnection() >> mockBulkConnection;
            mockBulkConnection.createJob(_) >> mockJobInfo;

            mockBulkConnection.createBatchFromStream(_, _) >> mockBatchInfo;


            mockQueryResultList.getResult() >> [resultId];
            mockBulkConnection.getQueryResultStream(_, _, _) >> new ByteArrayInputStream('"fruit"\r\n"orange"'.getBytes());;
            List<Map<String,String>> result = webServiceClient.exportDataFromTable(tableName)

        then:
            assert result.size() == 1;
            assert result[0].get("fruit") == "orange";
    }

    def "Should Export Selected ColumnData From Table HashMap"() {
        given:
            SalesforceConnectionClient mockConnectionClient = Mock(SalesforceConnectionClient);
            BulkConnection mockBulkConnection = Mock(BulkConnection);

            JobInfo mockJobInfo = Mock(JobInfo)
            BatchInfo mockBatchInfo = Mock(BatchInfo)

            PartnerConnection mockPartnerConnection = Mock(PartnerConnection);
            DescribeSObjectResult mockDescribeSObjectResult = Mock(DescribeSObjectResult);
            Field mockField = Mock(Field);
            Field[] mockFields = [mockField];

            QueryResultList mockQueryResultList =  Mock(QueryResultList);
            SalesforceWebServiceClient webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)

        when:
            String resultId= 'z1x';
            String tableName = "ProductsTable"
            mockJobInfo.getId() >> '1234';
            mockBatchInfo.getId() >> '6789';

            mockConnectionClient.getSalesForceWebServicePartnerConnection() >> mockPartnerConnection;
            mockBulkConnection.getQueryResultList(_, _) >> mockQueryResultList;
            mockQueryResultList.getResult() >> [resultId];
            mockPartnerConnection.describeSObject(tableName) >> mockDescribeSObjectResult;
            mockDescribeSObjectResult.getFields() >> mockFields;
            mockField.getName() >> "fruit";


            mockConnectionClient.getSalesForceWebServiceBulkConnection() >> mockBulkConnection;
            mockBulkConnection.createJob(_) >> mockJobInfo;

            mockBulkConnection.createBatchFromStream(_, _) >> mockBatchInfo;


            mockQueryResultList.getResult() >> [resultId];
            mockBulkConnection.getQueryResultStream(_, _, _) >> new ByteArrayInputStream('"fruit"\r\n"orange"'.getBytes());;
            List<Map<String,String>> result = webServiceClient.exportDataFromTable(tableName, ["fruit"]);
        then:
            assert result.size() == 1;
            assert result[0].get("fruit") == "orange";
    }
}
