package com.mideo.salesforce

import com.sforce.soap.apex.ExecuteAnonymousResult
import com.sforce.soap.partner.FieldType;
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
        return salesforceConnectionClient
                .getSalesForceWebServicePartnerConnection()
                .describeSObject(targetObjectName)
                .getFields()
                .findAll { it.getType().equals(FieldType.address) ? [] : it}
                .collect{ it.getName() }

    }

    String createSObject(String sObjectName, Map<String, Object> data) throws ConnectionException {
        SObject sObject = new SObject(sObjectName);
        return salesforceConnectionClient
                .getSalesForceWebServicePartnerConnection()
                .create(buildSObject(sObject, data)).first().getId();


    }

    String updateSObject(String sObjectName, String id, Map<String, Object> data) throws ConnectionException {
        SObject sObject = new SObject(sObjectName);
        sObject.setId(id);

        return salesforceConnectionClient
                .getSalesForceWebServicePartnerConnection()
                .update(buildSObject(sObject, data)).first().getId();
    }

    Map<String, Object> retrieveSObject(String sObjectName, String id) throws ConnectionException {
        //get data columns
        List<String> columns = getDataColumns(sObjectName);

        SObject[] sObjects = salesforceConnectionClient
                .getSalesForceWebServicePartnerConnection()
                .retrieve(columns.join(','), sObjectName, id);

        return columns.collectEntries{
            [it, sObjects[0].getField(it)]
        }


    }


    String deleteSObject(String id) throws ConnectionException {

        return salesforceConnectionClient
                .getSalesForceWebServicePartnerConnection()
                .delete(id).first().getId();

    }

    ExecuteAnonymousResult executeApexBlock(String apexCode){
        return salesforceConnectionClient
                .getSalesforceSoapConnection()
                .executeAnonymous(apexCode);
    }
}
