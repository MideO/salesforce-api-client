package com.github.mideo.salesforce

import com.jayway.restassured.response.Response
import com.jayway.restassured.specification.RequestSpecification
import com.sforce.soap.apex.ExecuteAnonymousResult
import com.sforce.soap.apex.SoapConnection
import com.sforce.soap.partner.FieldType
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException
import groovy.json.JsonOutput
import groovy.json.JsonSlurper


class SObjectApi {
    RequestSpecification requestSpecification;
    Object session;
    PartnerConnection partnerConnection;
    SoapConnection soapConnection;

    List<String> getDataColumns(String targetObjectName) throws ConnectionException {
        return  partnerConnection
                .describeSObject(targetObjectName)
                .getFields()
                .findAll { it.getType().equals(FieldType.address) ? [] : it}
                .collect{ it.getName() }
    }

    String createSObject(String sObjectName, Object deserializableObject) throws ConnectionException {
        Response response =  requestSpecification.expect().statusCode(201).given()
                .baseUri(session.instance_url)
                .body(JsonOutput.toJson(deserializableObject))
                .header("Accept", "application/json")
                .header('Authorization', "Bearer ${session.access_token}")
                .header("Content-Type", "application/json")
                .post("/sobjects/${sObjectName}");

        return new JsonSlurper().parseText(response.print()).id;
    }


    String createOrUpdateSObject(String sObjectName, String externalIdFieldName, Object deserializableObject) throws Exception {
        Response response =  requestSpecification
                .baseUri(session.instance_url as String)
                .body(JsonOutput.toJson(deserializableObject))
                .header("Accept", "application/json")
                .header('Authorization', "Bearer ${session.access_token}")
                .header("Content-Type", "application/json")
                .post("/sobjects/${sObjectName}/${externalIdFieldName}/${URLEncoder.encode(deserializableObject.id,"UTF-8")}/?_HttpMethod=PATCH");
        int succeeded = ((int) response.statusCode()/100)
        if( succeeded != 2) {
            throw new Exception("Failed to create or update ${sObjectName}")
        }

        return new JsonSlurper().parseText(response.print()).id;


    }



    String updateSObject(String sObjectName, String id, Object deserializableObject) throws ConnectionException {
        Response response =  requestSpecification.expect().statusCode(204).given()
                .baseUri(session.instance_url as String)
                .body(JsonOutput.toJson(deserializableObject))
                .header("Accept", "application/json")
                .header('Authorization', "Bearer ${session.access_token}")
                .header("Content-Type", "application/json")
                .post("/sobjects/${sObjectName}/${id}?_HttpMethod=PATCH");

        return new JsonSlurper().parseText(response.print()).id;
    }

    Map<String, Object> retrieveSObject(String sObjectName, String id) throws ConnectionException {
        //get data columns
        List<String> columns = getDataColumns(sObjectName);

        SObject[] sObjects = partnerConnection
                .retrieve(columns.join(','), sObjectName, id);

        return columns.collectEntries{
            [it, sObjects[0].getField(it)]
        }
    }


    String deleteSObject(String id) throws ConnectionException {

        return partnerConnection
                .delete(id).first().getId();

    }

    List<Map<String, Object>> executeSoqlQuery(String queryString){

        return partnerConnection
                .query(queryString)
                .getRecords().collect{
                    it.children.collectEntries{
                        [it.getName().getLocalPart(), it.value]
                    }
                }
    }

    ExecuteAnonymousResult executeApexBlock(String apexCode){
        return soapConnection.executeAnonymous(apexCode);
    }
}
