package salesforce

import com.sforce.async.BatchInfo
import com.sforce.async.BulkConnection
import com.sforce.async.JobInfo
import org.mockito.Mockito
import spock.lang.Specification

import static org.mockito.Mockito.when


class WebServiceClientTest extends Specification {
    ConnectionClient mockConnectionClient
    BulkConnection mockBulkConnection
    JobInfo mockJobInfo
    BatchInfo mockBatchInfo
    InputStream inputStream

    void setup() {
        mockConnectionClient = Mockito.mock(ConnectionClient.class)
        mockBulkConnection = Mockito.mock(BulkConnection.class)
        mockJobInfo = Mockito.mock(JobInfo.class)
        mockBatchInfo = Mockito.mock(BatchInfo.class)
        inputStream = new ByteArrayInputStream("abcd".getBytes())
        when(mockJobInfo.getId()).thenReturn("1234")

        when(mockConnectionClient.getSalesForceWebServiceBulkConnection()).thenReturn(mockBulkConnection)
        when(mockBulkConnection.createJob(Mockito.any(JobInfo.class))).thenReturn(mockJobInfo)
        when(mockBulkConnection.closeJob(mockJobInfo.getId())).thenReturn(mockJobInfo)
        when(mockBulkConnection.createBatchFromStream(mockJobInfo, inputStream)).thenReturn(mockBatchInfo)

    }


    def "Should publish CSV to salesforce table"() {

        given:
            WebServiceClient webServiceClient = new WebServiceClient(mockConnectionClient)

        when:
            boolean result = webServiceClient.publishCsvToTable(inputStream, "AccountTable")

        then:
            assert result

    }
}
