package com.github.mideo.salesforce.ant

import com.github.mideo.salesforce.PublishResult
import com.github.mideo.salesforce.SalesforceWebServiceClient
import spock.lang.Specification


class PublishTaskTest extends Specification {
    File csvFile
    def path

    def setup() {
        csvFile = new File('foo.csv')
        csvFile.write('abc')
        path = csvFile.getCanonicalPath()
    }

    def cleanup() {

        csvFile.delete()
    }



    def "Should Publish CSV"() {
        given:
            def task = Spy(PublishTask);
            task.csvFilesRelativePath = path.minus('foo.csv')
            def mockWebClient = Mock(SalesforceWebServiceClient);
            PublishResult publishResult = Mock(PublishResult);

        when:

            task.createWebClient() >> mockWebClient;
            mockWebClient.publishCsvToTable(_, _) >> publishResult
            publishResult.isPublished() >> true
            task.execute();

        then:
            assert task.result.isPublished();
    }
}
