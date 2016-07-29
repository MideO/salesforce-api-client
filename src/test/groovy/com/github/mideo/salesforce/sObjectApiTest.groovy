package com.github.mideo.salesforce

import com.jayway.restassured.response.Response
import com.jayway.restassured.specification.RequestSpecification
import com.jayway.restassured.specification.ResponseSpecification
import com.sforce.soap.apex.ExecuteAnonymousResult
import com.sforce.soap.apex.SoapConnection
import com.sforce.soap.partner.DeleteResult
import com.sforce.soap.partner.DescribeSObjectResult
import com.sforce.soap.partner.FieldType
import com.sforce.soap.partner.PartnerConnection
import com.sforce.soap.partner.QueryResult
import com.sforce.soap.partner.sobject.SObject
import groovy.json.JsonOutput
import spock.lang.Specification

import com.sforce.soap.partner.Field

class sObjectApiTest extends Specification {

    def "Should Get Data Columns"() {
        given:
        def tableName = "Rubarb";
        def mockConnectionClient = Mock(SalesforceConnectionClient);
        def mockPartnerConnection = Mock(PartnerConnection);
        def mockDescribeSObjectResult = Mock(DescribeSObjectResult);
        def mockField = Mock(Field);
        def mockFields = [mockField];
        def objectApi = new SObjectApi()

        when:
        mockConnectionClient.getSalesForceWebServicePartnerConnection() >> mockPartnerConnection;
        mockPartnerConnection.describeSObject(tableName) >> mockDescribeSObjectResult;
        mockDescribeSObjectResult.getFields() >> mockFields;
        mockField.getName() >> "fruit";
        mockField.getType() >> FieldType.string;
        objectApi.partnerConnection = mockConnectionClient.getSalesForceWebServicePartnerConnection()
        def resultName = objectApi.getDataColumns(tableName);

        then:
        assert resultName.size() == 1;
        assert resultName.contains("fruit");
    }

    class MockAccount {
        def name;
        def email;
        def id;
    }

    def "Should Create SObject"() {
        given:
            def mockAccount = new MockAccount(name: "testName bazz", email: "x@y.com");
            def sObjectName = "Account";

            def mockRequestSpecification = Mock(RequestSpecification)
            def objectApi = Spy(SObjectApi);
            def response = Mock(Response)


        when:
            objectApi.getSpecification() >> mockRequestSpecification
            mockRequestSpecification.baseUri(_) >> mockRequestSpecification
            mockRequestSpecification.body(_) >> mockRequestSpecification
            mockRequestSpecification.header(_, _) >> mockRequestSpecification
            response.statusCode() >> 201
            response.print() >> JsonOutput.toJson([id: 'weweweer', success: true, errors: []])
            mockRequestSpecification.post("/sobjects/${sObjectName}") >> response
            objectApi.restExplorerUrl = 'tryghjkl';
            objectApi.sessionToken = 'tryghjkl';


            def Id = objectApi.createSObject(sObjectName, mockAccount);

        then:
            assert Id == "weweweer";
    }

    def "Should Update SObject"() {

        given:
            def mockAccount = new MockAccount(name: "testName bazz", email: "x@y.com");
            def sObjectName = "Account";

            def mockRequestSpecification = Mock(RequestSpecification)
            def objectApi = Spy(SObjectApi);
            def response = Mock(Response)
            def id = 'wkhjwjek';


        when:
            objectApi.getSpecification() >> mockRequestSpecification
            mockRequestSpecification.baseUri(_) >> mockRequestSpecification
            mockRequestSpecification.body(_) >> mockRequestSpecification
            mockRequestSpecification.header(_, _) >> mockRequestSpecification
            response.statusCode() >> 201
            response.print() >> JsonOutput.toJson([id: 'fugazi', success: true, errors: []])
            mockRequestSpecification.post("/sobjects/${sObjectName}/${id}?_HttpMethod=PATCH") >> response
            objectApi.restExplorerUrl = 'tryghjkl';
            objectApi.sessionToken = 'tryghjkl';

        def resultId = objectApi.updateSObject(sObjectName, id, mockAccount);

        then:
        assert resultId == id;
    }

    def "Should Create Or Update SObject"() {

        given:
            def mockAccount = new MockAccount(name: "testName bazz", email: "x@y.com", id: 'wkhjwjek');
            def sObjectName = "Account";

            def mockRequestSpecification = Mock(RequestSpecification)
            def mockResponseSpecification = Mock(ResponseSpecification)
            def objectApi = Spy(SObjectApi);
            def response = Mock(Response)

        when:
            objectApi.getSpecification() >> mockRequestSpecification

            mockRequestSpecification.expect() >> mockResponseSpecification
            mockResponseSpecification.statusCode(_) >> mockResponseSpecification
            mockResponseSpecification.given() >> mockRequestSpecification
            mockRequestSpecification.baseUri(_) >> mockRequestSpecification
            mockRequestSpecification.body(_) >> mockRequestSpecification
            mockRequestSpecification.header(_, _) >> mockRequestSpecification
            response.print() >> JsonOutput.toJson([id: 'fugazi', success: true, errors: []])
            response.statusCode() >> statusCode

            mockRequestSpecification.post("/sobjects/${sObjectName}/Id/${URLEncoder.encode(mockAccount.id, "UTF-8")}/?_HttpMethod=PATCH") >> response
            objectApi.restExplorerUrl = 'tryghjkl';
            objectApi.sessionToken = 'tryghjkl';
            def Id = objectApi.createOrUpdateSObject(sObjectName, 'Id', mockAccount);

        then:
            assert Id == result;

        where:
            statusCode || result
            201        || 'wkhjwjek'
            204        || 'fugazi'

    }


    def "Should Create Or Update SObject throw Exception"() {

        given:
        def mockAccount = new MockAccount(name: "testName bazz", email: "x@y.com", id: 'wkhjwjek');
        def sObjectName = "Account";

        def mockRequestSpecification = Mock(RequestSpecification)
        def mockResponseSpecification = Mock(ResponseSpecification)
        def objectApi = new SObjectApi();
        def response = Mock(Response)



        when:
        mockRequestSpecification.expect() >> mockResponseSpecification
        mockResponseSpecification.statusCode(_) >> mockResponseSpecification
        mockResponseSpecification.given() >> mockRequestSpecification
        mockRequestSpecification.baseUri(_) >> mockRequestSpecification
        mockRequestSpecification.body(_) >> mockRequestSpecification
        mockRequestSpecification.header(_, _) >> mockRequestSpecification
        response.print() >> "Unauthorized"
        response.statusCode() >> 401

        mockRequestSpecification.post("/sobjects/${sObjectName}/Id/${URLEncoder.encode(mockAccount.id, "UTF-8")}/?_HttpMethod=PATCH") >> response
        objectApi.requestSpecification = mockRequestSpecification
        objectApi.restExplorerUrl = 'tryghjkl';
        objectApi.sessionToken = 'tryghjkl';
        objectApi.createOrUpdateSObject(sObjectName, 'Id', mockAccount);

        then:
        thrown Exception
    }

    def "Should Create Or Update SObject throw Exception if id not set"() {

        given:
        def mockAccount = new MockAccount(name: "testName bazz", email: "x@y.com",);
        def sObjectName = "Account";

        def mockRequestSpecification = Mock(RequestSpecification)
        def mockResponseSpecification = Mock(ResponseSpecification)
        def objectApi = new SObjectApi();
        def response = Mock(Response)



        when:
        mockRequestSpecification.expect() >> mockResponseSpecification
        mockResponseSpecification.statusCode(_) >> mockResponseSpecification
        mockResponseSpecification.given() >> mockRequestSpecification
        mockRequestSpecification.baseUri(_) >> mockRequestSpecification
        mockRequestSpecification.body(_) >> mockRequestSpecification
        mockRequestSpecification.header(_, _) >> mockRequestSpecification
        response.print() >> "Unauthorized"
        response.statusCode() >> 401

        mockRequestSpecification.post("/sobjects/${sObjectName}/Id/${URLEncoder.encode(mockAccount.id, "UTF-8")}/?_HttpMethod=PATCH") >> response
        objectApi.requestSpecification = mockRequestSpecification
        objectApi.restExplorerUrl = 'tryghjkl';
        objectApi.sessionToken = 'tryghjkl';
        objectApi.createOrUpdateSObject(sObjectName, 'Id', mockAccount);

        then:
        thrown Exception
    }

    def "Should execute soql query"() {

        given:
        def mockPartnerConnection = Mock(PartnerConnection);
        def mockQueryResult = Mock(QueryResult);
        def objectApi = new SObjectApi();
        def sObject = new SObject();
        sObject.setField('abc', 123);

        when:

        mockPartnerConnection.query(_) >> mockQueryResult;
        mockQueryResult.getRecords() >> [sObject];
        objectApi.partnerConnection = mockPartnerConnection
        def result = objectApi.executeSoqlQuery('abc123');

        then:
        assert result.get(0).keySet().contains('abc');
        assert result.get(0).values().contains(123);
    }

    def "Should Retrieve SObject"() {
        given:
        def sObject = new SObject("Mide");
        sObject.setSObjectField("car", "Fiat Panda");
        sObject.setId("fakeId");
        def sObjects = [sObject];
        def mockDescribeSObjectResult = Mock(DescribeSObjectResult);
        def mockField = Mock(Field);
        def mockFields = [mockField];
        def mockPartnerConnection = Mock(PartnerConnection);
        def objectApi = new SObjectApi();


        when:

        mockPartnerConnection.describeSObject("Mide") >> mockDescribeSObjectResult;
        mockDescribeSObjectResult.getFields() >> mockFields;
        mockField.getName() >> "car";
        mockField.getType() >> FieldType.string;
        def ids = ["fakeId"]
        mockPartnerConnection.retrieve(_, "Mide", ids) >> sObjects;

        objectApi.partnerConnection = mockPartnerConnection

        def resultMap = objectApi.retrieveSObject("Mide", "fakeId");

        then:
        assert resultMap.get("car") == "Fiat Panda";
    }


    def "Should Delete SObject"() {
        given:
        def mockPartnerConnection = Mock(PartnerConnection);
        def id = "fakeid";
        def deleteResult = new DeleteResult(id: "fakeId");
        def deleteResults = [deleteResult];

        def objectApi = new SObjectApi();

        when:
        mockPartnerConnection.delete(_) >> deleteResults;
        objectApi.partnerConnection = mockPartnerConnection;
        def Id = objectApi.deleteSObject(id);

        then:
        assert Id == "fakeId";
    }

    def "Should execute Anonymous Apex"() {
        given:
        def mockSoapConnection = Mock(SoapConnection);
        def executeAnonymousResult = new ExecuteAnonymousResult(success: true);
        def objectApi = new SObjectApi();

        when:
        mockSoapConnection.executeAnonymous("abc") >> executeAnonymousResult;
        objectApi.soapConnection = mockSoapConnection
        def result = objectApi.executeApexBlock("abc");

        then:
        assert result.success;
    }

}
