package com.github.mideo.salesforce

import com.sforce.soap.apex.ExecuteAnonymousResult
import com.sforce.soap.partner.FieldType;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException
import org.codehaus.jackson.map.ObjectMapper;


class SObjectApi {
    SalesforceConnectionClient salesforceConnectionClient;

    static ObjectMapper mapper = new ObjectMapper();

    static SObject[] buildSObject(SObject sObject, Object data) {

        mapper.convertValue(data, Map.class).each{
            k,v -> sObject.setSObjectField((String)k, v)
        }

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

    String createSObject(String sObjectName, Object deserializableObject) throws ConnectionException {
        SObject sObject = new SObject(sObjectName);
        return salesforceConnectionClient
                .getSalesForceWebServicePartnerConnection()
                .create(buildSObject(sObject, deserializableObject)).first().getId();


    }


    String createOrUpdateSObject(String sObjectName, String externalIdFieldName, Object deserializableObject) throws ConnectionException {
        SObject sObject = new SObject(sObjectName);
        return salesforceConnectionClient
                .getSalesForceWebServicePartnerConnection()
                .upsert(externalIdFieldName, buildSObject(sObject, deserializableObject)).first().getId();
    }



    String updateSObject(String sObjectName, String id, Object deserializableObject) throws ConnectionException {
        SObject sObject = new SObject(sObjectName);
        sObject.setId(id);

        return salesforceConnectionClient
                .getSalesForceWebServicePartnerConnection()
                .update(buildSObject(sObject, deserializableObject)).first().getId();
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

    List<Map<String, Object>> executeSoqlQuery(String queryString){

        return salesforceConnectionClient
                .getSalesForceWebServicePartnerConnection()
                .query(queryString)
                .getRecords().collect{
                    it.children.collectEntries{
                        [it.getName().getLocalPart(), it.value]
                    }
                }
    }

    ExecuteAnonymousResult executeApexBlock(String apexCode){
        return salesforceConnectionClient
                .getSalesforceSoapConnection()
                .executeAnonymous(apexCode);
    }
}
