package com.mideo.salesforce;


import com.sforce.soap.partner.Field;
import com.sforce.ws.ConnectionException;

import java.util.ArrayList;
import java.util.List;



public class ObjectDescriber {
    private SalesforceConnectionClient salesforceConnectionClient;

    public ObjectDescriber withSalesforceClient(SalesforceConnectionClient salesforceConnectionClient) {
        this.salesforceConnectionClient = salesforceConnectionClient;
        return this;
    }


    public List<String> getDataColumns(String targetObjectName) throws ConnectionException {
        Field[] fields = salesforceConnectionClient
                .getSalesForceWebServicePartnerConnection()
                .describeSObject(targetObjectName)
                .getFields();

        List<String> fieldNames = new ArrayList<>();
        for (Field field: fields){
            fieldNames.add(field.getName());
        }
        return fieldNames;
    }
}
