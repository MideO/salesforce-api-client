package com.mideo.salesforce;


import com.sforce.async.*;

import java.io.InputStream;


public class SalesforceWebServiceClient {

    private SalesforceConnectionClient salesforceConnectionClient;

    public SalesforceWebServiceClient(SalesforceConnectionClient salesforceConnectionClient) {
        this.salesforceConnectionClient = salesforceConnectionClient;
    }


    public void publishCsvToTable(InputStream inputStream, String tableName) throws AsyncApiException {
        JobInfo jobInfo = new Job()
                .withSalesforceClient(salesforceConnectionClient)
                .newJob(tableName)
                .setOperation(OperationEnum.insert)
                .setContentType(ContentType.CSV)
                .create();

        new Batch()
                .withSalesforceClient(salesforceConnectionClient)
                .addJob(jobInfo)
                .withCsvInputStream(inputStream)
                .createStream()
                .finaliseJob();
    }
}
