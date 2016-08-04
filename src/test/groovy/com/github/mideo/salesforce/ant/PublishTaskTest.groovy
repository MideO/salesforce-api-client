package com.github.mideo.salesforce.ant

import com.github.mideo.salesforce.PublishResult
import com.github.mideo.salesforce.SalesforceWebServiceClient
import spock.lang.Specification


class PublishTaskTest extends Specification {
    File csvFile
    def path

    def setup() {
        csvFile = new File('foo.csv')
       csvFile.write('abc,abbb,sds,sdsss,ssfffff\n123,,123,sddf,\r\n')
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

        when:
            task.createWebClient() >> mockWebClient;
            mockWebClient.createObject(_,_)>> '123'
            task.execute();

        then:
            assert task.publishId == '123';
    }
}
