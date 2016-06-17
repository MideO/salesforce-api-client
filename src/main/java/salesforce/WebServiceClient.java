package salesforce;


import com.sforce.async.*;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


public class WebServiceClient {

    private ConnectionClient connectionClient;

    public WebServiceClient(ConnectionClient connectionClient) {
        this.connectionClient = connectionClient;
    }


    public BatchInfo publishCsvStringToTable(String csvPayloadString, String tableName) throws AsyncApiException, FileNotFoundException {
        InputStream inputStream = new ByteArrayInputStream(csvPayloadString.getBytes());
        Job salesforceJob = new Job();
        Batch salesforceBatch = new Batch();
        System.out.println("Writing data to "+ tableName);

        JobInfo jobInfo = salesforceJob
                .newJob(tableName)
                .setOperation(OperationEnum.insert)
                .setContentType(ContentType.CSV)
                .create(connectionClient);

        BatchInfo batchInfo = salesforceBatch.addJob(jobInfo)
                .wtihCsvInputStream(inputStream)
                .create(connectionClient);
        salesforceJob.finishJob(connectionClient);
        return batchInfo;
    }
}
