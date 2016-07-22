package com.github.mideo.salesforce

import com.sforce.async.BatchInfo
import com.sforce.async.BatchStateEnum
import com.sforce.async.BulkConnection
import com.sforce.async.JobInfo
import com.sforce.async.JobStateEnum
import com.sforce.async.QueryResultList
import com.sforce.soap.apex.ExecuteAnonymousResult
import com.sforce.soap.apex.SoapConnection
import com.sforce.soap.partner.QueryResult
import com.sforce.soap.partner.DeleteResult
import com.sforce.soap.partner.DescribeSObjectResult
import com.sforce.soap.partner.Field
import com.sforce.soap.partner.FieldType
import com.sforce.soap.partner.PartnerConnection
import com.sforce.soap.partner.sobject.SObject
import spock.lang.Specification


class SalesforceWebServiceClientTest extends Specification {

    def "Should set publishStatusCheckTimeout"() {
        given:
            def mockConnectionClient = Mock(SalesforceConnectionClient)
            def webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)

        when:
            webServiceClient.setPublishStatusCheckTimeout(2000);

        then:
            assert webServiceClient.publishStatusCheckTimeout == 2000;
    }
    def "Should publish CSV to salesforce table"() {

        given:
            def mockConnectionClient = Mock(SalesforceConnectionClient)
            def mockBulkConnection = Mock(BulkConnection)
            def mockJobInfo = Mock(JobInfo)
            def mockBatchInfo = Mock(BatchInfo)
            def inputStream = new ByteArrayInputStream('abcd'.getBytes())

        when:
            mockJobInfo.getId() >> '1234';
            mockJobInfo.getState() >> JobStateEnum.Closed
            mockJobInfo.getObject() >> 'AccountTable'
            mockBatchInfo.getState() >> BatchStateEnum.Completed
            mockBulkConnection.createJob(_) >> mockJobInfo
            mockBulkConnection.closeJob(mockJobInfo.getId()) >> mockJobInfo
            mockConnectionClient.getSalesForceWebServiceBulkConnection() >> mockBulkConnection
            mockConnectionClient.getRestExplorerEndpoint() >> 'http://gfhjk'
            mockBulkConnection.createBatchFromStream(mockJobInfo, inputStream) >> mockBatchInfo

            def publishResult = new SalesforceWebServiceClient(mockConnectionClient)
                    .publishCsvToTable(inputStream, 'AccountTable')


        then:
            assert publishResult.isPublished();

    }

    def "Should get Published Data Status"() {
        given:
            def mockConnectionClient = Mock(SalesforceConnectionClient)
            def mockBulkConnection = Mock(BulkConnection)
            def mockBatchInfo = Mock(BatchInfo)


        when:
            mockBatchInfo.getJobId() >> '1234'
            mockBatchInfo.getId() >> '6789';
            mockBatchInfo.getState() >> BatchStateEnum.InProgress
            mockBulkConnection.getBatchInfo(mockBatchInfo.getJobId(), mockBatchInfo.getId()) >> mockBatchInfo
            mockConnectionClient.getSalesForceWebServiceBulkConnection() >> mockBulkConnection

        then:
            assert new SalesforceWebServiceClient(mockConnectionClient).getPublishedDataStatus(mockBatchInfo.getJobId(), mockBatchInfo.getId()) == 'InProgress'
    }

    def "Should Export Data From Table HashMap"() {
        given:
            def mockConnectionClient = Mock(SalesforceConnectionClient);
            def mockBulkConnection = Mock(BulkConnection);

            def mockJobInfo = Mock(JobInfo)
            def mockBatchInfo = Mock(BatchInfo)

            def mockPartnerConnection = Mock(PartnerConnection);
            def mockDescribeSObjectResult = Mock(DescribeSObjectResult);
            def mockField = Mock(Field);
            def mockFields = [mockField];

            def mockQueryResultList =  Mock(QueryResultList);

        when:
            def resultId= 'z1x';
            def tableName = "ProductsTable"
            mockJobInfo.getId() >> '1234';
            mockBatchInfo.getId() >> '6789';

            mockConnectionClient.getSalesForceWebServicePartnerConnection() >> mockPartnerConnection;
            mockBulkConnection.getQueryResultList(_, _) >> mockQueryResultList;
            mockQueryResultList.getResult() >> [resultId];
            mockPartnerConnection.describeSObject(tableName) >> mockDescribeSObjectResult;
            mockDescribeSObjectResult.getFields() >> mockFields;
            mockField.getName() >> "fruit";
            mockField.getType() >> FieldType.string;


            mockConnectionClient.getSalesForceWebServiceBulkConnection() >> mockBulkConnection;
            mockBulkConnection.createJob(_) >> mockJobInfo;

            mockBulkConnection.createBatchFromStream(_, _) >> mockBatchInfo;
            mockBatchInfo.getState() >> BatchStateEnum.Completed;
            mockBulkConnection.getBatchInfo(_, _) >> mockBatchInfo;


            mockQueryResultList.getResult() >> [resultId];
            mockBulkConnection.getQueryResultStream(_, _, _) >> new ByteArrayInputStream('"fruit"\r\n"orange"'.getBytes());;
            def result = new SalesforceWebServiceClient(mockConnectionClient).exportDataFromTable(tableName)

        then:
            assert result.size() == 1;
            assert result[0].get("fruit") == "orange";
    }

    def "Should Export Selected ColumnData From Table HashMap"() {
        given:
            def mockConnectionClient = Mock(SalesforceConnectionClient);
            def mockBulkConnection = Mock(BulkConnection);

            def mockJobInfo = Mock(JobInfo)
            def mockBatchInfo = Mock(BatchInfo)

            def mockPartnerConnection = Mock(PartnerConnection);
            def mockDescribeSObjectResult = Mock(DescribeSObjectResult);
            def mockField = Mock(Field);
            def mockFields = [mockField];

            def mockQueryResultList =  Mock(QueryResultList);


        when:
            def resultId= 'z1x';
            def tableName = "ProductsTable"
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
            def result = new SalesforceWebServiceClient(mockConnectionClient).exportDataFromTable(tableName, ["fruit"]);


        then:
            assert result.size() == 1;
            assert result[0].get("fruit") == "orange";
    }

    def "Should Export filtered Selected ColumnData From Table HashMap"() {
        given:
            def mockConnectionClient = Mock(SalesforceConnectionClient);
            def mockBulkConnection = Mock(BulkConnection);

            def mockJobInfo = Mock(JobInfo)
            def mockBatchInfo = Mock(BatchInfo)

            def mockPartnerConnection = Mock(PartnerConnection);
            def mockDescribeSObjectResult = Mock(DescribeSObjectResult);
            def mockField = Mock(Field);
            def mockFields = [mockField];

            def mockQueryResultList =  Mock(QueryResultList);


        when:
            def resultId= 'z1x';
            def tableName = "ProductsTable"
            mockJobInfo.getId() >> '1234';
            mockBatchInfo.getId() >> '6789';
            def filters = ["fruit":"orane"];
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
            def result = new SalesforceWebServiceClient(mockConnectionClient).exportDataFromTable(tableName, ["fruit"], filters);

        then:
            assert result.size() == 1;
            assert result[0].get("fruit") == "orange";
    }


    def "Should throw exception if batch query fails"() {
        given:
            def mockConnectionClient = Mock(SalesforceConnectionClient);
            def mockBulkConnection = Mock(BulkConnection);

            def mockJobInfo = Mock(JobInfo)
            def mockBatchInfo = Mock(BatchInfo)

            def mockPartnerConnection = Mock(PartnerConnection);
            def mockDescribeSObjectResult = Mock(DescribeSObjectResult);
            def mockField = Mock(Field);
            def mockFields = [mockField];

            def mockQueryResultList =  Mock(QueryResultList);

        when:
            def resultId= 'z1x';
            def tableName = "ProductsTable"
            mockJobInfo.getId() >> '1234';
            mockBatchInfo.getId() >> '6789';
            def filters = ["fruit":"orane"];
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
            new SalesforceWebServiceClient(mockConnectionClient).exportDataFromTable(tableName, ["fruit"], filters);

        then:
            thrown SalesforceApiOperationException;
    }


    def "Should Export filtered Data From Table HashMap"() {
        given:
            def mockConnectionClient = Mock(SalesforceConnectionClient);
            def mockBulkConnection = Mock(BulkConnection);

            def mockJobInfo = Mock(JobInfo)
            def mockBatchInfo = Mock(BatchInfo)

            def mockPartnerConnection = Mock(PartnerConnection);
            def mockDescribeSObjectResult = Mock(DescribeSObjectResult);
            def mockField = Mock(Field);
            def mockFields = [mockField];

            def mockQueryResultList =  Mock(QueryResultList);


        when:
            def resultId= 'z1x';
            def tableName = "ProductsTable"
            mockJobInfo.getId() >> '1234';
            mockBatchInfo.getId() >> '6789';
            Map<String, String> filters = ["fruit":"orane"];
            mockConnectionClient.getSalesForceWebServicePartnerConnection() >> mockPartnerConnection;
            mockBulkConnection.getQueryResultList(_, _) >> mockQueryResultList;
            mockQueryResultList.getResult() >> [resultId];
            mockPartnerConnection.describeSObject(tableName) >> mockDescribeSObjectResult;
            mockDescribeSObjectResult.getFields() >> mockFields;
            mockField.getName() >> "fruit";
            mockField.getType() >> FieldType.string;


            mockConnectionClient.getSalesForceWebServiceBulkConnection() >> mockBulkConnection;
            mockBulkConnection.createJob(_) >> mockJobInfo;

            mockBulkConnection.createBatchFromStream(_, _) >> mockBatchInfo;


            mockQueryResultList.getResult() >> [resultId];
            mockBulkConnection.getQueryResultStream(_, _, _) >> new ByteArrayInputStream('"fruit"\r\n"orange"'.getBytes());;
            mockBatchInfo.getState() >> BatchStateEnum.Completed;
            mockBulkConnection.getBatchInfo(_, _) >> mockBatchInfo;
            def webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)
            def result = webServiceClient.exportDataFromTable(tableName, filters);

        then:
            assert result.size() == 1;
            assert result[0].get("fruit") == "orange";
            assert webServiceClient.publishStatusCheckTimeout == 30000;
    }

    def "Should create Object"() {
        given:
            def sObjectName = "Alastair";
            def data = ["car":"abarth","insurance":"10m"];
            def mockConnectionClient = Mock(SalesforceConnectionClient);
            def webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)
            def mockObjectApi = Mock(SObjectApi);

        when:
            webServiceClient.sObjectApi = mockObjectApi
            webServiceClient.createObject(sObjectName, data);


        then:
            1 * mockObjectApi.createSObject(sObjectName, data)
    }

    def "Should update Object"() {
        given:
            def sObjectName = "Alastair";
            def data = ["car":"abarth","insurance":"10m"];
            def mockConnectionClient = Mock(SalesforceConnectionClient);
            def webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)
            def mockObjectApi = Mock(SObjectApi);
            def id = "dfghj";

        when:
            webServiceClient.sObjectApi = mockObjectApi
            webServiceClient.updateObject(sObjectName, id, data);


        then:
            1 * mockObjectApi.updateSObject(sObjectName, id, data)
    }



    class MockContact {
        def name;
        def email;
    }

    def "Should create Object form POJO"() {
        given:
            def sObjectName = "Contact"
            def contact = new MockContact(name:"test name",email: "a@b.com")
            def mockConnectionClient = Mock(SalesforceConnectionClient)
            def webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)
            def mockObjectApi = Mock(SObjectApi);

        when:
            webServiceClient.sObjectApi = mockObjectApi
            webServiceClient.createObject(sObjectName, contact);

        then:
            1 * mockObjectApi.createSObject(sObjectName, contact)
    }

    def "Should update Object from POJO"() {
        given:
            def sObjectName = "Contact"
            def contact = new MockContact(name:"test name",email: "a@b.com")
            def mockConnectionClient = Mock(SalesforceConnectionClient);
            def webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)
            def mockObjectApi = Mock(SObjectApi);
            def id = "dfghj";

        when:
            webServiceClient.sObjectApi = mockObjectApi
            webServiceClient.updateObject("Contact", id, contact);


        then:
            1 * mockObjectApi.updateSObject(sObjectName, id, contact)
    }


    def "Should create or update Object from POJO"() {
        given:
            def sObjectName = "Contact"
            def contact = new MockContact(name:"test name",email: "a@b.com")
            def mockConnectionClient = Mock(SalesforceConnectionClient);
            def webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)
            def mockObjectApi = Mock(SObjectApi);
            def id = "dfghj";

        when:
            webServiceClient.sObjectApi = mockObjectApi
            webServiceClient.createOrUpdateObject(sObjectName,'Id', contact);

        then:
            1 * mockObjectApi.createOrUpdateSObject(sObjectName, 'Id', contact)
    }

    def "Should retrieve salesforce object as a map"() {
        given:
            def sObject = new SObject("Mide");
            sObject.setSObjectField("car", "Fiat Panda");
            sObject.setId("fakeId");
            def sObjects = [sObject];
            def mockConnectionClient = Mock(SalesforceConnectionClient);
            def mockPartnerConnection = Mock(PartnerConnection);
            def mockDescribeSObjectResult = Mock(DescribeSObjectResult);
            def mockField = Mock(Field);
            def mockFields = [mockField];



        when:
            mockConnectionClient.getSalesForceWebServicePartnerConnection() >> mockPartnerConnection;
            mockPartnerConnection.describeSObject("Mide") >> mockDescribeSObjectResult;
            mockDescribeSObjectResult.getFields() >> mockFields;
            mockField.getName() >> "car";
            mockField.getType() >> FieldType.string;
            def ids = ["fakeId"]
            mockPartnerConnection.retrieve(_, "Mide", ids) >> sObjects;
            def webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)
            def resultMap  = webServiceClient.retrieveObject("Mide", "fakeId");


        then:
            assert resultMap.get("car").toString() == "Fiat Panda";
    }

    def "Should delete Object"() {
        given:

            def mockConnectionClient = Mock(SalesforceConnectionClient);
            def mockPartnerConnection = Mock(PartnerConnection);
            def deleteResult= new DeleteResult( id: "fakeId");
            def id = "evenFakerId";

        when:
            mockConnectionClient.getSalesForceWebServicePartnerConnection() >> mockPartnerConnection;
            mockPartnerConnection.delete(_) >> deleteResult;
            def webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)
            def resultID =  webServiceClient.deleteObject(id);


        then:
            assert resultID == "fakeId";
    }

    def "Should execute Anonymous Apex"() {
        given:

            def mockConnectionClient = Mock(SalesforceConnectionClient);
            def mockSoapConnection = Mock(SoapConnection);
            def executeAnonymousResult= new ExecuteAnonymousResult( success: true);

        when:
            mockConnectionClient.getSalesforceSoapConnection() >> mockSoapConnection;
            mockSoapConnection.executeAnonymous("abcs") >> executeAnonymousResult;
            def webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)
            def result = webServiceClient.executeApexBlock("abcs");


        then:
            assert result.success;

    }


    def "Should execute Soql Query"() {
        given:

            def mockConnectionClient = Mock(SalesforceConnectionClient);
            def mockPartnerConnection = Mock(PartnerConnection);
            def mockQueryResult = Mock(QueryResult);
            def sObject = new SObject();
            sObject.setField('abc', 123);



        when:
            mockConnectionClient.getSalesForceWebServicePartnerConnection() >> mockPartnerConnection
            mockPartnerConnection.query(_) >> mockQueryResult;
            mockQueryResult.getRecords() >> [sObject];
            def webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)
            def result = webServiceClient .executeSoqlQuery('abc123');


        then:
            assert result.get(0).keySet().contains('abc');
            assert result.get(0).values().contains(123);

    }
}
