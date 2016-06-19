package salesforce

import com.sforce.async.BatchInfo
import com.sforce.async.BulkConnection
import com.sforce.async.JobInfo
import org.mockito.Mockito
import spock.lang.Specification

import static org.mockito.Mockito.when


class BatchTest extends Specification {
    Batch batch
    JobInfo jobInfo
    InputStream inputStream
    SalesforceConnectionClient mockConnectionClient
    BulkConnection mockBulkConnection
    BatchInfo mockBatchInfo


    void setup() {
        batch = new Batch()
        jobInfo = new JobInfo()
        inputStream = new ByteArrayInputStream("abcd".getBytes());
        mockConnectionClient = Mockito.mock(SalesforceConnectionClient.class);
        mockBulkConnection = Mockito.mock(BulkConnection.class);
        mockBatchInfo = Mockito.mock(BatchInfo.class);
        when(mockConnectionClient.getSalesForceWebServiceBulkConnection()).thenReturn(mockBulkConnection)
        when(mockBulkConnection.createBatchFromStream(jobInfo, inputStream)).thenReturn(mockBatchInfo)

    }

    def "Should Add Job"() {
        when:
            batch.addJob(jobInfo)
        then:
            assert batch.job == jobInfo
    }

    def "Should Add Input Stream"() {
        when:
            batch.withCsvInputStream(inputStream)
        then:
            assert batch.csvInputStream == inputStream

    }

    def "Should Create BatchInfo"() {
        when:
            BatchInfo batchInfo = batch.addJob(jobInfo)
                    .withCsvInputStream(inputStream)
                    .create(mockConnectionClient)
        then:
            assert batchInfo == mockBatchInfo
    }
}
