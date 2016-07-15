package com.mideo.salesforce

import com.sforce.soap.apex.ExecuteAnonymousResult
import com.sforce.soap.apex.SoapConnection
import com.sforce.soap.partner.DeleteResult
import com.sforce.soap.partner.DescribeSObjectResult
import com.sforce.soap.partner.FieldType
import com.sforce.soap.partner.PartnerConnection
import com.sforce.soap.partner.SaveResult
import com.sforce.soap.partner.sobject.SObject
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
            def objectApi = new SObjectApi(salesforceConnectionClient: mockConnectionClient);

        when:
            mockConnectionClient.getSalesForceWebServicePartnerConnection() >> mockPartnerConnection;
            mockPartnerConnection.describeSObject(tableName) >> mockDescribeSObjectResult;
            mockDescribeSObjectResult.getFields() >> mockFields;
            mockField.getName() >> "fruit";
            mockField.getType() >> FieldType.string;
            def resultName = objectApi.getDataColumns(tableName);

        then:
            assert resultName.size() == 1;
            assert resultName.contains("fruit");
    }

    def "Should Create SObject"() {
        given:
            def sObjectName = "Alastair";
            def data = ["car":"abarth","insurance":"10m"];

            def mockConnectionClient = Mock(SalesforceConnectionClient);
            def mockPartnerConnection = Mock(PartnerConnection);
            def saveResult = new SaveResult( id: "fakeId");
            def results = [saveResult];
            def objectApi = new SObjectApi(salesforceConnectionClient: mockConnectionClient);

        when:
            mockConnectionClient.getSalesForceWebServicePartnerConnection() >> mockPartnerConnection;
            mockPartnerConnection.create(_) >> results;
            def Id =  objectApi.createSObject(sObjectName, data);

        then:
            assert Id == "fakeId";
    }

    def "Should Update SObject"() {
        given:
            def sObjectName = "Mide";
            def data = ["car":"Fiat Panda","insurance":"£2"];
            def mockConnectionClient = Mock(SalesforceConnectionClient);
            def mockPartnerConnection = Mock(PartnerConnection);
            def id = "fakeid";
            def saveResult = new SaveResult( id: "fakeId");
            def results = [saveResult];
            def objectApi = new SObjectApi(salesforceConnectionClient: mockConnectionClient);

        when:
            mockConnectionClient.getSalesForceWebServicePartnerConnection() >> mockPartnerConnection;
            mockPartnerConnection.update(_) >> results;
            def Id =  objectApi.updateSObject(sObjectName, id, data);

        then:
        assert Id == "fakeId";
    }

    def "Should Retrieve SObject"() {
        given:
            def sObject = new SObject("Mide");
            sObject.setSObjectField("car", "Fiat Panda");
            sObject.setId("fakeId");
            def sObjects = [sObject];
            def mockConnectionClient = Mock(SalesforceConnectionClient);
            def mockDescribeSObjectResult = Mock(DescribeSObjectResult);
            def mockField = Mock(Field);
            def mockFields = [mockField];
            def mockPartnerConnection = Mock(PartnerConnection);
            def objectApi = new SObjectApi(salesforceConnectionClient: mockConnectionClient);


        when:
            mockConnectionClient.getSalesForceWebServicePartnerConnection() >> mockPartnerConnection;
            mockPartnerConnection.describeSObject("Mide") >> mockDescribeSObjectResult;
            mockDescribeSObjectResult.getFields() >> mockFields;
            mockField.getName() >> "car";
            mockField.getType() >> FieldType.string;
            def ids = ["fakeId"]
            mockPartnerConnection.retrieve(_, "Mide", ids) >> sObjects;

            def resultMap =  objectApi.retrieveSObject("Mide", "fakeId");

        then:
            assert resultMap.get("car") == "Fiat Panda";
    }


    def "Should Delete SObject"() {
        given:
            def mockConnectionClient = Mock(SalesforceConnectionClient);
            def mockPartnerConnection = Mock(PartnerConnection);
            def id = "fakeid";
            def deleteResult= new DeleteResult( id: "fakeId");
            def deleteResults= [deleteResult];
            def objectApi = new SObjectApi(salesforceConnectionClient: mockConnectionClient);

        when:
            mockConnectionClient.getSalesForceWebServicePartnerConnection() >> mockPartnerConnection;
            mockPartnerConnection.delete(_) >> deleteResults;

            def Id =  objectApi.deleteSObject(id);

        then:
            assert Id == "fakeId";
    }

    def "Should execute Anonymous Apex"() {
        given:
            def mockConnectionClient = Mock(SalesforceConnectionClient);
            def mockSoapConnection = Mock(SoapConnection);
            def executeAnonymousResult= new ExecuteAnonymousResult( success: true);
            def objectApi = new SObjectApi(salesforceConnectionClient: mockConnectionClient);

        when:
            mockConnectionClient.getSalesforceSoapConnection() >> mockSoapConnection;
            mockSoapConnection.executeAnonymous("abc") >> executeAnonymousResult;

            def result =  objectApi.executeApexBlock("abc");

        then:
            assert result.success;
    }

}
