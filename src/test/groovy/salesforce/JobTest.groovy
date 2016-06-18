package salesforce

import com.sforce.async.BulkConnection
import com.sforce.async.ContentType
import com.sforce.async.JobInfo
import com.sforce.async.OperationEnum
import org.mockito.Mockito
import spock.lang.Specification

import static org.mockito.Mockito.when


class JobTest extends Specification {
    Job job
    ConnectionClient mockConnectionClient
    BulkConnection mockBulkConnection


    JobInfo mockJobInfo


    void setup(){
        job =  new Job()
        mockJobInfo = Mockito.mock(JobInfo.class)
        mockConnectionClient = Mockito.mock(ConnectionClient.class)
        mockBulkConnection = Mockito.mock(BulkConnection.class)
        when(mockConnectionClient.getSalesForceWebServiceBulkConnection()).thenReturn(mockBulkConnection)
        when(mockBulkConnection.createJob(Mockito.any(JobInfo.class))).thenReturn(mockJobInfo)
    }

    def "Should create new job"() {
        when:
            job.newJob("jobby")
        then:
            assert job.jobInfo.object == "jobby"

    }

    def "Should set Operation"() {
        when:
            job.setOperation(OperationEnum.insert)
        then:
            assert job.jobInfo.operation == OperationEnum.insert

    }

    def "Should Set ContentType"() {
        when:
            job.setContentType(ContentType.CSV)
        then:
            assert job.jobInfo.contentType== ContentType.CSV

    }

    def "Create"() {
        when:
            JobInfo jobInfo = job.newJob("jobby")
                                .setOperation(OperationEnum.insert)
                                .setContentType(ContentType.CSV)
                                .create(mockConnectionClient);
        then:
            assert jobInfo == mockJobInfo


    }

    def "FinishJob"() {
        given:
            job.newJob("jobby")
                .setOperation(OperationEnum.insert)
                .setContentType(ContentType.CSV)
                .create(mockConnectionClient);
        when:
            boolean result = job.finishJob(mockConnectionClient)

        then:
            assert result


    }
}
