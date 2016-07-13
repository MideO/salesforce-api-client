package com.mideo.salesforce

import com.sforce.async.BatchInfo
import com.sforce.async.BatchStateEnum
import com.sforce.async.BulkConnection
import com.sforce.async.JobInfo
import com.sforce.async.JobStateEnum
import com.sforce.async.QueryResultList
import com.sforce.soap.partner.DeleteResult
import com.sforce.soap.partner.DescribeSObjectResult
import com.sforce.soap.partner.Field
import com.sforce.soap.partner.PartnerConnection
import com.sforce.soap.partner.SaveResult
import com.sforce.soap.partner.sobject.SObject
import spock.lang.Specification


class SalesforceWebServiceClientTest extends Specification {

    def "Should set publishStatusCheckTimeout"() {
        given:
            SalesforceConnectionClient mockConnectionClient = Mock(SalesforceConnectionClient)
            SalesforceWebServiceClient webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)

        when:
            webServiceClient.setPublishStatusCheckTimeout(2000);

        then:
            assert webServiceClient.publishStatusCheckTimeout == 2000;
    }
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
            mockBatchInfo.getState() >> BatchStateEnum.Completed;
            mockBulkConnection.getBatchInfo(_, _) >> mockBatchInfo;


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
            mockBulkConnection.getQueryResultStream(_, _, _) >> new ByteArrayInputStream('"fruit"\r\n"orange"'.getBytes());
            mockBatchInfo.getState() >> BatchStateEnum.Completed;
            mockBulkConnection.getBatchInfo(_, _) >> mockBatchInfo;
            List<Map<String,String>> result = webServiceClient.exportDataFromTable(tableName, ["fruit"]);


        then:
            assert result.size() == 1;
            assert result[0].get("fruit") == "orange";
    }

    def "Should Export filtered Selected ColumnData From Table HashMap"() {
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
            Map<String, String> filters = ["fruit":"orane"];
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
            mockBatchInfo.getState() >> BatchStateEnum.Completed;
            mockBulkConnection.getBatchInfo(_, _) >> mockBatchInfo;
            List<Map<String,String>> result = webServiceClient.exportDataFromTable(tableName, ["fruit"], filters);

        then:
            assert result.size() == 1;
            assert result[0].get("fruit") == "orange";
    }


    def "Should throw exception if batch query fails"() {
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
            Map<String, String> filters = ["fruit":"orane"];
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
            mockBatchInfo.getState() >> BatchStateEnum.Failed;
            mockBulkConnection.getBatchInfo(_, _) >> mockBatchInfo;
            webServiceClient.exportDataFromTable(tableName, ["fruit"], filters);

        then:
            thrown FailedBulkOperationException;
    }


    def "Should Export filtered Data From Table HashMap"() {
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
            Map<String, String> filters = ["fruit":"orane"];
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
            mockBatchInfo.getState() >> BatchStateEnum.Completed;
            mockBulkConnection.getBatchInfo(_, _) >> mockBatchInfo;
            List<Map<String,String>> result = webServiceClient.exportDataFromTable(tableName, filters);

        then:
            assert result.size() == 1;
            assert result[0].get("fruit") == "orange";
    }

    def "Should create Object"() {
        given:
            String sObjectName = "Alastair";
            Map<String, String> data = ["car":"abarth","insurance":"10m"];
            SalesforceConnectionClient mockConnectionClient = Mock(SalesforceConnectionClient);
            PartnerConnection mockPartnerConnection = Mock(PartnerConnection);
            SaveResult saveResult = new SaveResult( id: "fakeId");
            SaveResult[] results = [saveResult];
            SalesforceWebServiceClient webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)

        when:
            mockConnectionClient.getSalesForceWebServicePartnerConnection() >> mockPartnerConnection;
            mockPartnerConnection.create(_) >> results;
            String Id =  webServiceClient.createObject(sObjectName, data);


        then:
            assert Id == "fakeId";
    }

    def "Should update Object"() {
        given:
            String sObjectName = "Mide";
            Map<String, String> data = ["car":"Fiat Panda","insurance":"£2"];
            SalesforceConnectionClient mockConnectionClient = Mock(SalesforceConnectionClient);
            PartnerConnection mockPartnerConnection = Mock(PartnerConnection);
            SaveResult saveResult = new SaveResult( id: "fakeId");
            SaveResult[] results = [saveResult];
            SalesforceWebServiceClient webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)
            String id = "evenFakerId";

        when:
            mockConnectionClient.getSalesForceWebServicePartnerConnection() >> mockPartnerConnection;
            mockPartnerConnection.update(_) >> results;
            String resultID =  webServiceClient.updateObject(sObjectName, id, data);


        then:
            assert resultID == "fakeId";
    }

    def "Should retrieve salesforce object as a map"() {
        given:
            SObject sObject = new SObject("Mide");
            sObject.setSObjectField("car", "Fiat Panda");
            sObject.setId("fakeId");
            SObject[] sObjects = [sObject];
            SObjectApi objectApi = new SObjectApi();
            SalesforceConnectionClient mockConnectionClient = Mock(SalesforceConnectionClient);
            PartnerConnection mockPartnerConnection = Mock(PartnerConnection);
            DescribeSObjectResult mockDescribeSObjectResult = Mock(DescribeSObjectResult);
            Field mockField = Mock(Field);
            Field[] mockFields = [mockField];
            SalesforceWebServiceClient webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)


        when:
            mockConnectionClient.getSalesForceWebServicePartnerConnection() >> mockPartnerConnection;
            mockPartnerConnection.describeSObject("Mide") >> mockDescribeSObjectResult;
            mockDescribeSObjectResult.getFields() >> mockFields;
            mockField.getName() >> "car";
            String[] ids = ["fakeId"]
            mockPartnerConnection.retrieve(_, "Mide", ids) >> sObjects;

            Map<String, Objects> resultMap  = webServiceClient.retrieveObject("Mide", "fakeId");


        then:
            assert resultMap.get("car").toString() == "Fiat Panda";
    }

    def "Should delete Object"() {
        given:

            SalesforceConnectionClient mockConnectionClient = Mock(SalesforceConnectionClient);
            PartnerConnection mockPartnerConnection = Mock(PartnerConnection);
            DeleteResult deleteResult= new DeleteResult( id: "fakeId");
            DeleteResult[] deleteResults = [deleteResult];
            SalesforceWebServiceClient webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)
            String id = "evenFakerId";

        when:
            mockConnectionClient.getSalesForceWebServicePartnerConnection() >> mockPartnerConnection;
            mockPartnerConnection.delete(_) >> deleteResult;
            String resultID =  webServiceClient.deleteObject(id);


        then:
            assert resultID == "fakeId";
    }
}
