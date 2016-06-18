package salesforce

import com.sforce.async.BatchInfo
import com.sforce.async.BulkConnection
import com.sforce.async.JobInfo
import org.mockito.Mockito
import spock.lang.Specification

import static org.mockito.Mockito.when


class WebServiceClientTest extends Specification {
    private ConnectionClient mockConnectionClient = Mockito.mock(ConnectionClient.class);
    private BulkConnection mockBulkConnection = Mockito.mock(BulkConnection.class);
    private JobInfo mockJobInfo = Mockito.mock(JobInfo.class);
    private BatchInfo mockBatchInfo = Mockito.mock(BatchInfo.class);


    def "PublishCsvStringToTable"() {

        given:
            InputStream inputStream = new ByteArrayInputStream("abcd".getBytes());
            when(mockJobInfo.getId()).thenReturn("1234")

            when(mockConnectionClient.getSalesForceWebServiceBulkConnection()).thenReturn(mockBulkConnection)
            when(mockBulkConnection.createJob(Mockito.any(JobInfo.class))).thenReturn(mockJobInfo)
            when(mockBulkConnection.closeJob(mockJobInfo.getId())).thenReturn(mockJobInfo)
            when(mockBulkConnection.createBatchFromStream(mockJobInfo,inputStream )).thenReturn(mockBatchInfo)

            WebServiceClient webServiceClient = new WebServiceClient(mockConnectionClient)

        when:
            boolean result = webServiceClient.publishCsvToTable(inputStream, "AccountTable")

        then:
            assert result

    }
}
