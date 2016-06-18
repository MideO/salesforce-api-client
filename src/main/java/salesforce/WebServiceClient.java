package salesforce;


import com.sforce.async.*;

import java.io.FileNotFoundException;
import java.io.InputStream;


public class WebServiceClient {

    private ConnectionClient connectionClient;
    Job salesforceJob;
    Batch salesforceBatch;

    public WebServiceClient(ConnectionClient connectionClient) {
        this.connectionClient = connectionClient;
    }


    public BatchInfo publishCsvToTable(InputStream inputStream, String tableName) throws AsyncApiException, FileNotFoundException {
        salesforceJob = new Job();
        salesforceBatch = new Batch();

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
