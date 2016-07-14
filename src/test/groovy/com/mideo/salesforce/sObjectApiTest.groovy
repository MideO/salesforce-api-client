package com.mideo.salesforce

import com.sforce.soap.partner.DeleteResult
import com.sforce.soap.partner.DescribeSObjectResult
import com.sforce.soap.partner.PartnerConnection
import com.sforce.soap.partner.SaveResult
import com.sforce.soap.partner.sobject.SObject
import spock.lang.Specification

import com.sforce.soap.partner.Field

class sObjectApiTest extends Specification {

    def "Should Get Data Columns"() {
        given:
            String tableName = "Rubarb";
            SalesforceConnectionClient mockConnectionClient = Mock(SalesforceConnectionClient);
            PartnerConnection mockPartnerConnection = Mock(PartnerConnection);
            DescribeSObjectResult mockDescribeSObjectResult = Mock(DescribeSObjectResult);
            Field mockField = Mock(Field);
            Field[] mockFields = [mockField];
            SObjectApi objectApi = new SObjectApi(salesforceConnectionClient: mockConnectionClient);

        when:
            mockConnectionClient.getSalesForceWebServicePartnerConnection() >> mockPartnerConnection;
            mockPartnerConnection.describeSObject(tableName) >> mockDescribeSObjectResult;
            mockDescribeSObjectResult.getFields() >> mockFields;
            mockField.getName() >> "fruit";
            List<String> resultName = objectApi.getDataColumns(tableName);

        then:
            assert resultName.size() == 1;
            assert resultName.contains("fruit");
    }

    def "Should Create SObject"() {
        given:
            String sObjectName = "Alastair";
            Map<String, String> data = ["car":"abarth","insurance":"10m"];

            SalesforceConnectionClient mockConnectionClient = Mock(SalesforceConnectionClient);
            PartnerConnection mockPartnerConnection = Mock(PartnerConnection);
            SaveResult saveResult = new SaveResult( id: "fakeId");
            SaveResult[] results = [saveResult];
            SObjectApi objectApi = new SObjectApi(salesforceConnectionClient: mockConnectionClient);

        when:
            mockConnectionClient.getSalesForceWebServicePartnerConnection() >> mockPartnerConnection;
            mockPartnerConnection.create(_) >> results;
            String Id =  objectApi.createSObject(sObjectName, data);

        then:
            assert Id == "fakeId";
    }

    def "Should Update SObject"() {
        given:
        String sObjectName = "Mide";
        Map<String, String> data = ["car":"Fiat Panda","insurance":"£2"];
        SalesforceConnectionClient mockConnectionClient = Mock(SalesforceConnectionClient);
        PartnerConnection mockPartnerConnection = Mock(PartnerConnection);
        String id = "fakeid";
        SaveResult saveResult = new SaveResult( id: "fakeId");
        SaveResult[] results = [saveResult];
        SObjectApi objectApi = new SObjectApi(salesforceConnectionClient: mockConnectionClient);

        when:
        mockConnectionClient.getSalesForceWebServicePartnerConnection() >> mockPartnerConnection;
        mockPartnerConnection.update(_) >> results;

        String Id =  objectApi.updateSObject(sObjectName, id, data);

        then:
        assert Id == "fakeId";
    }

    def "Should Retrieve SObject"() {
        given:
            SObject sObject = new SObject("Mide");
            sObject.setSObjectField("car", "Fiat Panda");
            sObject.setId("fakeId");
            SObject[] sObjects = [sObject];
            SalesforceConnectionClient mockConnectionClient = Mock(SalesforceConnectionClient);
            DescribeSObjectResult mockDescribeSObjectResult = Mock(DescribeSObjectResult);
            Field mockField = Mock(Field);
            Field[] mockFields = [mockField];
            PartnerConnection mockPartnerConnection = Mock(PartnerConnection);
            SObjectApi objectApi = new SObjectApi(salesforceConnectionClient: mockConnectionClient);


        when:
            mockConnectionClient.getSalesForceWebServicePartnerConnection() >> mockPartnerConnection;
            mockPartnerConnection.describeSObject("Mide") >> mockDescribeSObjectResult;
            mockDescribeSObjectResult.getFields() >> mockFields;
            mockField.getName() >> "car";
            String[] ids = ["fakeId"]
            mockPartnerConnection.retrieve(_, "Mide", ids) >> sObjects;

            Map<String, Object> resultMap =  objectApi.retrieveSObject("Mide", "fakeId");

        then:
            assert resultMap.get("car") == "Fiat Panda";
    }


    def "Should Delete SObject"() {
        given:
            SalesforceConnectionClient mockConnectionClient = Mock(SalesforceConnectionClient);
            PartnerConnection mockPartnerConnection = Mock(PartnerConnection);
            String id = "fakeid";
            DeleteResult deleteResult= new DeleteResult( id: "fakeId");
            DeleteResult[] deleteResults= [deleteResult];
            SObjectApi objectApi = new SObjectApi(salesforceConnectionClient: mockConnectionClient);

        when:
            mockConnectionClient.getSalesForceWebServicePartnerConnection() >> mockPartnerConnection;
            mockPartnerConnection.delete(_) >> deleteResults;

            String Id =  objectApi.deleteSObject(id);

        then:
            assert Id == "fakeId";
    }

}
