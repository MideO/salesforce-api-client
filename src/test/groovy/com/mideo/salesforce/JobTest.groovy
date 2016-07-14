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
            job.newJobInfo("jobby")
        then:
            assert job.jobInfo.object == "jobby"

    }

    def "Should set Operation and Content Type for Insertion"() {
        given:
            Job job = new Job()
        when:
            job.toInsert(ContentType.CSV)
        then:
            assert job.jobInfo.operation == OperationEnum.insert
            assert job.jobInfo.contentType== ContentType.CSV
    }

    def "Should set Operation and Content Type for Query"() {
        given:
            Job job = new Job()
        when:
            job.toQuery(ContentType.XML)
        then:
            assert job.jobInfo.operation == OperationEnum.query
            assert job.jobInfo.contentType== ContentType.XML
    }


    def "Should set Job as Concurrent"() {
        given:
            Job job = new Job();
        when:
            job.withParallelConcurrencyMode();
        then:
            assert job.jobInfo.concurrencyMode__is_set
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

            Job job = new Job(salesforceConnectionClient: mockConnectionClient)
                    .newJobInfo("jobby")
                    .setOperation(OperationEnum.insert)
                    .setContentType(ContentType.CSV)
            JobInfo jobInfo = job.create();
        then:
            assert jobInfo == jobInfo


    }
}
