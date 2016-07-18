package com.mideo.salesforce

import com.sforce.async.BatchInfo
import com.sforce.async.BatchStateEnum
import com.sforce.async.BulkConnection
import com.sforce.async.JobInfo
import com.sforce.async.JobStateEnum
import com.sforce.async.QueryResultList
import com.sforce.soap.apex.ExecuteAnonymousResult
import com.sforce.soap.apex.SoapConnection
import com.sforce.soap.partner.UpsertResult
import com.sforce.soap.partner.DeleteResult
import com.sforce.soap.partner.DescribeSObjectResult
import com.sforce.soap.partner.Field
import com.sforce.soap.partner.FieldType
import com.sforce.soap.partner.PartnerConnection
import com.sforce.soap.partner.SaveResult
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
            def webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)

        when:
            mockJobInfo.getId() >> '1234';
            mockJobInfo.getState() >> JobStateEnum.Closed
            mockJobInfo.getObject() >> 'AccountTable'
            mockBatchInfo.getState() >> BatchStateEnum.Completed
            mockBulkConnection.createJob(_) >> mockJobInfo
            mockBulkConnection.closeJob(mockJobInfo.getId()) >> mockJobInfo
            mockConnectionClient.getSalesForceWebServiceBulkConnection() >> mockBulkConnection
            mockBulkConnection.createBatchFromStream(mockJobInfo, inputStream) >> mockBatchInfo

            def publishResult = webServiceClient.publishCsvToTable(inputStream, 'AccountTable')


        then:
            assert publishResult.isPublished();

    }

    def "Should get Published Data Status"() {
        given:
            def mockConnectionClient = Mock(SalesforceConnectionClient)
            def mockBulkConnection = Mock(BulkConnection)
            def mockBatchInfo = Mock(BatchInfo)
            def webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)

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
            def mockConnectionClient = Mock(SalesforceConnectionClient);
            def mockBulkConnection = Mock(BulkConnection);

            def mockJobInfo = Mock(JobInfo)
            def mockBatchInfo = Mock(BatchInfo)

            def mockPartnerConnection = Mock(PartnerConnection);
            def mockDescribeSObjectResult = Mock(DescribeSObjectResult);
            def mockField = Mock(Field);
            def mockFields = [mockField];

            def mockQueryResultList =  Mock(QueryResultList);
            def webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)

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
            def result = webServiceClient.exportDataFromTable(tableName)

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
            def webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)

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
            def result = webServiceClient.exportDataFromTable(tableName, ["fruit"]);


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
            def webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)

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
            def result = webServiceClient.exportDataFromTable(tableName, ["fruit"], filters);

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
            def webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)


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
            webServiceClient.exportDataFromTable(tableName, ["fruit"], filters);

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
            def webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)

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
            def mockPartnerConnection = Mock(PartnerConnection);
            def saveResult = new SaveResult( id: "fakeId");
            def results = [saveResult];
            def webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)

        when:
            mockConnectionClient.getSalesForceWebServicePartnerConnection() >> mockPartnerConnection;
            mockPartnerConnection.create(_) >> results;
            def Id =  webServiceClient.createObject(sObjectName, data);


        then:
            assert Id == "fakeId";
    }

    def "Should update Object"() {
        given:
            def sObjectName = "Mide";
            def data = ["car":"Fiat Panda","insurance":"£2"];
            def mockConnectionClient = Mock(SalesforceConnectionClient);
            def mockPartnerConnection = Mock(PartnerConnection);
            def saveResult = new SaveResult( id: "fakeId");
            def results = [saveResult];
            def webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)
            def id = "evenFakerId";

        when:
            mockConnectionClient.getSalesForceWebServicePartnerConnection() >> mockPartnerConnection;
            mockPartnerConnection.update(_) >> results;
            def resultID =  webServiceClient.updateObject(sObjectName, id, data);


        then:
            assert resultID == "fakeId";
    }



    class MockContact {
        def name;
        def email;
    }

    def "Should create Object form POJO"() {
        given:
            def sObjectName = "Contact"
            def contact = new MockContact(name:"test name",email: "a@b.com")
            def mockConnectionClient = Mock(SalesforceConnectionClient);
            def mockPartnerConnection = Mock(PartnerConnection);
            def saveResult = new SaveResult( id: "fakeId");
            def results = [saveResult];
            def webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)

        when:
            mockConnectionClient.getSalesForceWebServicePartnerConnection() >> mockPartnerConnection;
            mockPartnerConnection.create(_) >> results;
            def Id =  webServiceClient.createObject(sObjectName, contact);


        then:
          assert Id == "fakeId";
    }

    def "Should update Object from POJO"() {
        given:

            def contact = new MockContact(name:"test name",email: "a@b.com")
            def mockConnectionClient = Mock(SalesforceConnectionClient);
            def mockPartnerConnection = Mock(PartnerConnection);
            def saveResult = new SaveResult( id: "fakeId");
            def results = [saveResult];
            def webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)
            def id = "evenFakerId";

        when:
            mockConnectionClient.getSalesForceWebServicePartnerConnection() >> mockPartnerConnection;
            mockPartnerConnection.update(_) >> results;
            def resultId =  webServiceClient.updateObject("Contact", id, contact);


        then:
            assert resultId == "fakeId";
    }


    def "Should create or update Object from POJO"() {
        given:
            def contact = new MockContact(name:"test name",email: "a@b.com")
            def mockConnectionClient = Mock(SalesforceConnectionClient);
            def mockPartnerConnection = Mock(PartnerConnection);
            def upsertResult = new UpsertResult( id: "fakeId");
            def results = [upsertResult];
            def webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)
            def id = "evenFakerId";

        when:
            mockConnectionClient.getSalesForceWebServicePartnerConnection() >> mockPartnerConnection;
            mockPartnerConnection.upsert('Id', _) >> results;
            def resultId =  webServiceClient.createOrUpdateObject("Contact", 'Id', contact);


        then:
            assert resultId == "fakeId";
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
            def webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)


        when:
            mockConnectionClient.getSalesForceWebServicePartnerConnection() >> mockPartnerConnection;
            mockPartnerConnection.describeSObject("Mide") >> mockDescribeSObjectResult;
            mockDescribeSObjectResult.getFields() >> mockFields;
            mockField.getName() >> "car";
            mockField.getType() >> FieldType.string;
            def ids = ["fakeId"]
            mockPartnerConnection.retrieve(_, "Mide", ids) >> sObjects;

            def resultMap  = webServiceClient.retrieveObject("Mide", "fakeId");


        then:
            assert resultMap.get("car").toString() == "Fiat Panda";
    }

    def "Should delete Object"() {
        given:

            def mockConnectionClient = Mock(SalesforceConnectionClient);
            def mockPartnerConnection = Mock(PartnerConnection);
            def deleteResult= new DeleteResult( id: "fakeId");
            def webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)
            def id = "evenFakerId";

        when:
            mockConnectionClient.getSalesForceWebServicePartnerConnection() >> mockPartnerConnection;
            mockPartnerConnection.delete(_) >> deleteResult;
            def resultID =  webServiceClient.deleteObject(id);


        then:
            assert resultID == "fakeId";
    }

    def "Should execute Anonymous Apex"() {
        given:

            def mockConnectionClient = Mock(SalesforceConnectionClient);
            def mockSoapConnection = Mock(SoapConnection);
            def executeAnonymousResult= new ExecuteAnonymousResult( success: true);
            def webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)


        when:
            mockConnectionClient.getSalesforceSoapConnection() >> mockSoapConnection;
            mockSoapConnection.executeAnonymous("abcs") >> executeAnonymousResult;
            def result = webServiceClient.executeApexBlock("abcs");


        then:
            assert result.success;

    }
}
