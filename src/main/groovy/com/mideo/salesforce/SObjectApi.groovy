package com.mideo.salesforce;

import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;


class SObjectApi {
    SalesforceConnectionClient salesforceConnectionClient;


    static SObject[] buildSObject(SObject sObject, Map<String, Object> data) {
        for(Map.Entry entry: data.entrySet()){
            sObject.setSObjectField(entry.getKey().toString(), entry.getValue());
        }

        data.each {k,v -> sObject.setSObjectField(k,v)};

        return [sObject]
    }

    List<String> getDataColumns(String targetObjectName) throws ConnectionException {
        return  salesforceConnectionClient
                .getSalesForceWebServicePartnerConnection()
                .describeSObject(targetObjectName)
                .getFields().collect {
                it.getName()
            }
    }

    String createSObject(String sObjectName, Map<String, Object> data) throws ConnectionException {
        //create sObject
        SObject sObject = new SObject(sObjectName);
        SObject[] sObjects = buildSObject(sObject, data);
        // perform Create
        return salesforceConnectionClient
                .getSalesForceWebServicePartnerConnection()
                .create(sObjects).first().getId();


    }

    String updateSObject(String sObjectName, String id, Map<String, Object> data) throws ConnectionException {
        //create sObject
        SObject sObject = new SObject(sObjectName);
        sObject.setId(id);
        SObject[] sObjects = buildSObject(sObject, data);
        // perform Update
        return salesforceConnectionClient
                .getSalesForceWebServicePartnerConnection()
                .update(sObjects).first().getId();
    }

    Map<String, Object> retrieveSObject(String sObjectName, String id) throws ConnectionException {
        //get data columns
        List<String> columns = getDataColumns(sObjectName);

        //perform retrieveObject
        SObject[] sObjects = salesforceConnectionClient
                .getSalesForceWebServicePartnerConnection()
                .retrieve(columns.join(','), sObjectName, id);

        return columns.collectEntries{
            [it, sObjects[0].getField(it)]
        }


    }


    String deleteSObject(String id) throws ConnectionException {
        //perform retrieveObject
        return salesforceConnectionClient
                .getSalesForceWebServicePartnerConnection()
                .delete(id).first().getId();

    }
}
