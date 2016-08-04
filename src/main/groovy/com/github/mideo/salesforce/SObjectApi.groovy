package com.github.mideo.salesforce

import com.jayway.restassured.RestAssured
import com.jayway.restassured.response.Response
import com.jayway.restassured.specification.RequestSpecification
import com.sforce.soap.apex.ExecuteAnonymousResult
import com.sforce.soap.apex.SoapConnection
import com.sforce.soap.partner.FieldType
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException
import groovy.json.JsonSlurper
import org.codehaus.jackson.map.ObjectMapper

import static groovy.json.JsonOutput.toJson


class SObjectApi {
    String restExplorerUrl, sessionToken;
    PartnerConnection partnerConnection;
    SoapConnection soapConnection;
    static ObjectMapper mapper = new ObjectMapper();

    static String deNulledJson(Object object){
        Map dataMap = mapper.convertValue(object, Map.class)

        return toJson(dataMap.findAll { it.value != null });

    }


    RequestSpecification getSpecification() {
        return RestAssured.given();
    }

    private static void validateResponse(Response response, String sObjectName){
        int succeeded = ((int) response.statusCode()/100)
        if( succeeded != 2) {
            throw new Exception("Failed to create or update ${sObjectName}\n Response Code ${response.statusCode()}\n Response Content: ${response.print()}")
        }
    }

    private static void validateResponse(Response response){
        int succeeded = ((int) response.statusCode()/100)
        if( succeeded != 2) {
            throw new Exception("Failed to retrieve data \n Response Code ${response.statusCode()}\n Response Content: ${response.print()}")
        }
    }

    List<String> getDataColumns(String targetObjectName) throws ConnectionException {
        Response response = getSpecification()
                .baseUri(restExplorerUrl)
                .header('Authorization', "Bearer ${sessionToken}")
                .get("/sobjects/${targetObjectName}/describe");
        validateResponse(response);
        return new JsonSlurper().parseText(response.getBody().asString()).fields.collect()
                .findAll { it.type == FieldType.address.name() ? [] : it
                }.collect{ it.name }
    }

    String createSObject(String sObjectName, Object deserializableObject) throws ConnectionException {
        Response response =  getSpecification()
                .baseUri(restExplorerUrl)
                .body(deNulledJson(deserializableObject))
                .header("Accept", "application/json")
                .header('Authorization', "Bearer ${sessionToken}")
                .header("Content-Type", "application/json")
                .post("/sobjects/${sObjectName}");
        validateResponse(response, sObjectName);
        return new JsonSlurper().parseText(response.body.asString()).id;
    }


    String createOrUpdateSObject(String sObjectName, String externalIdFieldName, Object deserializableObject) throws Exception {
        if( deserializableObject.id ==  null ) {
            throw new Exception("${sObjectName} must be set")
        }

        String id = deserializableObject.id
        deserializableObject.id=null
        Response response =  getSpecification()
                .baseUri(restExplorerUrl)
                .body(deNulledJson(deserializableObject))
                .header("Accept", "application/json")
                .header('Authorization', "Bearer ${sessionToken}")
                .header("Content-Type", "application/json")
                .post("/sobjects/${sObjectName}/${externalIdFieldName}/${URLEncoder.encode(id,"UTF-8")}/?_HttpMethod=PATCH");
        validateResponse(response, sObjectName);
        if( response.statusCode() == 204) {
            return new JsonSlurper().parseText(response.body.asString()).id;
        }
        return id;


    }



    String updateSObject(String sObjectName, String id, Object deserializableObject) throws ConnectionException {
        Response response = getSpecification()
                .baseUri(restExplorerUrl)
                .body(deNulledJson(deserializableObject))
                .header("Accept", "application/json")
                .header('Authorization', "Bearer ${sessionToken}")
                .header("Content-Type", "application/json")
                .post("/sobjects/${sObjectName}/${id}?_HttpMethod=PATCH");
        validateResponse(response, sObjectName);
        return id;
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

    List<Map<String, Object>> executeSoqlQuery(String queryString) throws Exception{
        Response response = getSpecification()
                .baseUri(restExplorerUrl)
                .header('Authorization', "Bearer ${sessionToken}")
                .get("/query/?q=${queryString}");
        validateResponse(response);
        return new JsonSlurper().parseText(response.body.asString()).records.each{
            it.remove('attributes')
        }.collect()
    }

    ExecuteAnonymousResult executeApexBlock(String apexCode){
        return soapConnection.executeAnonymous(apexCode);
    }
}
