package com.mideo.salesforce;


import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SObjectApi {
    private SalesforceConnectionClient salesforceConnectionClient;

    SObjectApi withSalesforceClient(SalesforceConnectionClient salesforceConnectionClient) {
        this.salesforceConnectionClient = salesforceConnectionClient;
        return this;
    }

    private SObject[] buildSObject(SObject sObject, Map<String, Object> data) {
        for(Map.Entry entry: data.entrySet()){
            sObject.setSObjectField(entry.getKey().toString(), entry.getValue());
        }
        return new SObject[]{sObject};
    }

    List<String> getDataColumns(String targetObjectName) throws ConnectionException {
        Field[] fields = salesforceConnectionClient
                .getSalesForceWebServicePartnerConnection()
                .describeSObject(targetObjectName)
                .getFields();

        List<String> fieldNames = new ArrayList<>();
        for (Field field : fields) {
            fieldNames.add(field.getName());
        }
        return fieldNames;
    }

    String createSObject(String sObjectName, Map<String, Object> data) throws ConnectionException {
        //create sObject
        SObject sObject = new SObject(sObjectName);
        SObject[] sObjects = buildSObject(sObject, data);
        // perform Create
        SaveResult[] saveResult = salesforceConnectionClient
                .getSalesForceWebServicePartnerConnection()
                .create(sObjects);

        // return result ID
        return saveResult[0].getId();
    }

    String updateSObject(String sObjectName, String id, Map<String, Object> data) throws ConnectionException {
        //create sObject
        SObject sObject = new SObject(sObjectName);
        sObject.setId(id);
        SObject[] sObjects = buildSObject(sObject, data);
        // perform Update
        SaveResult[] saveResult = salesforceConnectionClient
                .getSalesForceWebServicePartnerConnection()
                .update(sObjects);
        // return result ID
        return saveResult[0].getId();
    }

    Map<String, Object> retrieveSObject(String sObjectName, String id) throws ConnectionException {
        //get data columns
        List<String> columns = getDataColumns(sObjectName);

        //perform retrieve
        SObject[] sObjects = salesforceConnectionClient
                .getSalesForceWebServicePartnerConnection()
                .retrieve(StringUtils.join(columns, ','), sObjectName, new String[]{id});
        Map<String, Object> resultMap= new HashMap<>();

        //build map
        for(String column : columns) {
            resultMap.put(column, sObjects[0].getField(column));
        }
        return resultMap;
    }
}
