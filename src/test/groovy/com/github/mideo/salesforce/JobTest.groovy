package com.github.mideo.salesforce

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
            assert job.jobInfo.getConcurrencyMode()
    }
    def "Should Create JobInfo"() {
        given:
            BulkConnection mockBulkConnection = Mock(BulkConnection)
            JobInfo mockJobInfo = Mock(JobInfo)
        when:
            mockBulkConnection.createJob(_) >> mockJobInfo
            mockBulkConnection.closeJob(_) >> mockJobInfo

            Job job = new Job(bulkConnection: mockBulkConnection)
                    .newJobInfo("jobby")
                    .setOperation(OperationEnum.insert)
                    .setContentType(ContentType.CSV)
            JobInfo jobInfo = job.create();
        then:
            assert jobInfo == jobInfo


    }
}
