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

    def "Should Set SalesforceClient"() {
        given:
            Job job = new Job()
            SalesforceConnectionClient mockConnectionClient = Mock(SalesforceConnectionClient)
        when:
            job.withSalesforceClient(mockConnectionClient)
        then:
        assert job.salesforceConnectionClient== mockConnectionClient

    }

    def "Should Create JobInfo"() {
        given:
            SalesforceConnectionClient mockConnectionClient = Mock(SalesforceConnectionClient)
            BulkConnection mockBulkConnection = Mock(BulkConnection)
            JobInfo mockJobInfo = Mock(JobInfo)
        when:
            mockConnectionClient.getSalesForceWebServiceBulkConnection() >> mockBulkConnection
            mockBulkConnection.createJob(_) >> mockJobInfo
            mockConnectionClient.getSalesForceWebServiceBulkConnection().closeJob(_) >> mockJobInfo
            Job job = new Job()
                    .newJob("jobby")
                    .withSalesforceClient(mockConnectionClient)
                    .setOperation(OperationEnum.insert)
                    .setContentType(ContentType.CSV)
            JobInfo jobInfo = job.create();
        then:
            assert jobInfo == jobInfo


    }
}
