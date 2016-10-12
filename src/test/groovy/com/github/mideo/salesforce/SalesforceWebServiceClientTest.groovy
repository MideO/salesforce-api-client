package com.github.mideo.salesforce

import com.sforce.async.BatchInfo
import com.sforce.async.BatchStateEnum
import com.sforce.async.BulkConnection
import com.sforce.async.ContentType
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

    def "Should publish Data to salesforce table"() {

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
        mockBulkConnection.getBatchInfo(mockJobInfo.getId(), _) >> mockBatchInfo

        def publishResult = new SalesforceWebServiceClient(mockConnectionClient)
                .publishDataToTable(inputStream, 'AccountTable', ContentType.CSV)


        then:
        assert publishResult.isPublished();


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
        mockBulkConnection.getBatchInfo(mockJobInfo.getId(), _) >> mockBatchInfo

        def publishResult = new SalesforceWebServiceClient(mockConnectionClient)
                .publishCsvToTable(inputStream, 'AccountTable')

        then:
        assert publishResult.isPublished();
    }

    def "Should publish XML to salesforce table"() {


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
        mockBulkConnection.getBatchInfo(mockJobInfo.getId(), _) >> mockBatchInfo

        def publishResult = new SalesforceWebServiceClient(mockConnectionClient)
                .publishXMLToTable(inputStream, 'AccountTable')

        then:
        assert publishResult.isPublished();
    }


    def "Should publish JSON to salesforce table"() {


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
        mockBulkConnection.getBatchInfo(mockJobInfo.getId(), _) >> mockBatchInfo

        def publishResult = new SalesforceWebServiceClient(mockConnectionClient)
                .publishJsonToTable(inputStream, 'AccountTable')

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
        assert new SalesforceWebServiceClient(mockConnectionClient).getBatchDataStatus(mockBatchInfo.getJobId(), mockBatchInfo.getId()) == 'InProgress'
    }

    def "Should Export Data From Table "() {
        given:
        def mockConnectionClient = Mock(SalesforceConnectionClient)
        def webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)
        def mockObjectApi = Mock(SObjectApi);

        when:
        mockObjectApi.getDataColumns(_) >> ["fruit"]
        webServiceClient.sObjectApi = mockObjectApi
        webServiceClient.exportDataFromTable('acd');

        then:
        1 * mockObjectApi.executeSoqlQuery(URLEncoder.encode('SELECT fruit FROM acd'))

    }

    def "Should Export Selected ColumnData From Table "() {
        given:
        def mockConnectionClient = Mock(SalesforceConnectionClient)
        def webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)
        def mockObjectApi = Mock(SObjectApi);

        when:
        mockObjectApi.getDataColumns(_) >> ["fruit"]
        webServiceClient.sObjectApi = mockObjectApi
        webServiceClient.exportDataFromTable('acd', ["fruit"]);

        then:
        1 * mockObjectApi.executeSoqlQuery(URLEncoder.encode('SELECT fruit FROM acd'))

    }

    def "Should Export filtered Selected ColumnData From Table "() {
        given:
        def mockConnectionClient = Mock(SalesforceConnectionClient)
        def webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)
        def mockObjectApi = Mock(SObjectApi);

        when:
        mockObjectApi.getDataColumns(_) >> ["fruit"]
        webServiceClient.sObjectApi = mockObjectApi
        def filters = ["fruit": "orange"];
        webServiceClient.exportDataFromTable('acd', ["fruit"], filters);

        then:
        1 * mockObjectApi.executeSoqlQuery(URLEncoder.encode('SELECT fruit FROM acd WHERE fruit=\'orange\''))

    }


    def "Should throw exception if query fails"() {
        given:
        def mockConnectionClient = Mock(SalesforceConnectionClient)
        def webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)
        def mockObjectApi = Mock(SObjectApi);

        when:
        mockObjectApi.executeSoqlQuery(_) >> new Exception()
        webServiceClient.sObjectApi = mockObjectApi
        def filters = ["fruit": "orange"];
        webServiceClient.exportDataFromTable('addd', ["fruit"], filters);

        then:
        thrown SalesforceApiOperationException;
    }


    def "Should Export filtered Data From Table"() {
        given:
        def mockConnectionClient = Mock(SalesforceConnectionClient)
        def webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)
        def mockObjectApi = Mock(SObjectApi);

        when:
        Map<String, String> filters = ["fruit": "orange"];
        mockObjectApi.getDataColumns(_) >> ["fruit"]
        webServiceClient.sObjectApi = mockObjectApi
        webServiceClient.exportDataFromTable('acd', filters);

        then:
        1 * mockObjectApi.executeSoqlQuery(URLEncoder.encode('SELECT fruit FROM acd WHERE fruit=\'orange\''))

    }

    def "Should create Object"() {
        given:
        def sObjectName = "Alastair";
        def data = ["car": "abarth", "insurance": "10m"];
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
        def data = ["car": "abarth", "insurance": "10m"];
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
        def contact = new MockContact(name: "test name", email: "a@b.com")
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
        def contact = new MockContact(name: "test name", email: "a@b.com")
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
        def contact = new MockContact(name: "test name", email: "a@b.com")
        def mockConnectionClient = Mock(SalesforceConnectionClient);
        def webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)
        def mockObjectApi = Mock(SObjectApi);

        when:
        webServiceClient.sObjectApi = mockObjectApi
        webServiceClient.createOrUpdateObject(sObjectName, 'Id', contact);

        then:
        1 * mockObjectApi.createOrUpdateSObject(sObjectName, 'Id', contact)
    }

    def "Should retrieve salesforce object as a map"() {
        given:
        def mockConnectionClient = Mock(SalesforceConnectionClient);
        def mockObjectApi = Mock(SObjectApi);

        when:
        def webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)
        webServiceClient.sObjectApi = mockObjectApi
        webServiceClient.retrieveObject("Mide", "fakeId");

        then:
        1 * mockObjectApi.retrieveSObject("Mide", "fakeId")
    }

    def "Should delete Object"() {
        given:

        def mockConnectionClient = Mock(SalesforceConnectionClient);
        def mockPartnerConnection = Mock(PartnerConnection);
        def deleteResult = new DeleteResult(id: "fakeId");
        def id = "evenFakerId";

        when:
        mockConnectionClient.getSalesForceWebServicePartnerConnection() >> mockPartnerConnection;
        mockPartnerConnection.delete(_) >> deleteResult;
        def webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)
        def resultID = webServiceClient.deleteObject(id);


        then:
        assert resultID == "fakeId";
    }

    def "Should execute Anonymous Apex"() {
        given:

        def mockConnectionClient = Mock(SalesforceConnectionClient);
        def mockSoapConnection = Mock(SoapConnection);
        def executeAnonymousResult = new ExecuteAnonymousResult(success: true);

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
        def mockConnectionClient = Mock(SalesforceConnectionClient)
        def webServiceClient = new SalesforceWebServiceClient(mockConnectionClient)
        def mockObjectApi = Mock(SObjectApi);



        when:
        webServiceClient.sObjectApi = mockObjectApi
        webServiceClient.executeSoqlQuery('123344')


        then:
        1 * mockObjectApi.executeSoqlQuery('123344')

    }
}
