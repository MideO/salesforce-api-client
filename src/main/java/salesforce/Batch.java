package salesforce;

import com.sforce.async.AsyncApiException;
import com.sforce.async.BatchInfo;
import com.sforce.async.JobInfo;

import java.io.InputStream;


class Batch {

    private BatchInfo batch;
    private JobInfo job;
    private InputStream csvInputStream;

    public BatchInfo getBatch() {
        return batch;
    }

    public JobInfo getJob() {
        return job;
    }

    public InputStream getCsvInputStream() {
        return csvInputStream;
    }

    Batch(){
        batch = new BatchInfo();
    }

    Batch addJob(JobInfo job) {
        this.job = job;
        return this;
    }

    Batch wtihCsvInputStream(InputStream csvInputStream) {
        this.csvInputStream = csvInputStream;
        return this;
    }


    BatchInfo create(ConnectionClient connectionClient) throws AsyncApiException {
        batch = connectionClient.getSalesForceWebServiceBulkConnection().createBatchFromStream(job, csvInputStream);
        return batch;
    }
}

