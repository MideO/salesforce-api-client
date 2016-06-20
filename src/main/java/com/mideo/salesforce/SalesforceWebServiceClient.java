package com.mideo.salesforce;


import com.sforce.async.*;

import java.io.FileNotFoundException;
import java.io.InputStream;


public class SalesforceWebServiceClient {

    private SalesforceConnectionClient salesforceConnectionClient;
    Job salesforceJob;
    Batch salesforceBatch;

    public SalesforceWebServiceClient(SalesforceConnectionClient salesforceConnectionClient) {
        this.salesforceConnectionClient = salesforceConnectionClient;
    }


    public BatchInfo publishCsvToTable(InputStream inputStream, String tableName) throws AsyncApiException, FileNotFoundException {
        salesforceJob = new Job();
        salesforceBatch = new Batch();

        JobInfo jobInfo = salesforceJob
                .newJob(tableName)
                .setOperation(OperationEnum.insert)
                .setContentType(ContentType.CSV)
                .create(salesforceConnectionClient);

        BatchInfo batchInfo = salesforceBatch.addJob(jobInfo)
                .withCsvInputStream(inputStream)
                .create(salesforceConnectionClient);
        salesforceJob.finishJob(salesforceConnectionClient);
        return batchInfo;
    }
}
