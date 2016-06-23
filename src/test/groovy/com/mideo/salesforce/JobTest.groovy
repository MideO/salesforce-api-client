package com.mideo.salesforce

import com.sforce.async.BulkConnection
import com.sforce.async.ContentType
import com.sforce.async.JobInfo
import com.sforce.async.OperationEnum
import spock.lang.Specification


class JobTest extends Specification {


    def "Should create new job"() {
        given:
            Job job = new Job()
        when:
            job.newJob("jobby")
        then:
            assert job.jobInfo.object == "jobby"

    }

    def "Should set Operation"() {
        given:
            Job job = new Job()
        when:
            job.setOperation(OperationEnum.insert)
        then:
            assert job.jobInfo.operation == OperationEnum.insert

    }

    def "Should Set ContentType"() {
        given:
            Job job = new Job()
        when:
            job.setContentType(ContentType.CSV)
        then:
            assert job.jobInfo.contentType== ContentType.CSV

    }

    def "Should Create JobInfo"() {
        given:
            Job job = new Job()
            SalesforceConnectionClient mockConnectionClient = Mock(SalesforceConnectionClient)
            BulkConnection mockBulkConnection = Mock(BulkConnection)
            JobInfo mockJobInfo = Mock(JobInfo)
        when:
            mockConnectionClient.getSalesForceWebServiceBulkConnection() >> mockBulkConnection
            mockBulkConnection.createJob(_) >> mockJobInfo

            JobInfo jobInfo = job.newJob("jobby")
                                .setOperation(OperationEnum.insert)
                                .setContentType(ContentType.CSV)
                                .create(mockConnectionClient);
        then:
            assert jobInfo == jobInfo


    }

    def "Should finish salesforce jobInfo"() {
        given:
            Job job = new Job()
            SalesforceConnectionClient mockConnectionClient = Mock(SalesforceConnectionClient)
            BulkConnection mockBulkConnection = Mock(BulkConnection)
            JobInfo mockJobInfo = Mock(JobInfo)


        when:
            mockConnectionClient.getSalesForceWebServiceBulkConnection() >> mockBulkConnection
            mockBulkConnection.createJob(_) >> mockJobInfo
            mockConnectionClient.getSalesForceWebServiceBulkConnection().closeJob(_) >> mockJobInfo
            job.newJob("jobby")
                .setOperation(OperationEnum.insert)
                .setContentType(ContentType.CSV)
                .create(mockConnectionClient);

        then:
            assert mockJobInfo == job.finishJob(mockConnectionClient)


    }
}
