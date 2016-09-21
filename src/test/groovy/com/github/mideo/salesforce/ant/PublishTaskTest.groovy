package com.github.mideo.salesforce.ant

import com.github.mideo.salesforce.SalesforceWebServiceClient
import spock.lang.Specification


class PublishTaskTest extends Specification {
    File csvFile
    def path

    def setup() {
        new File(System.getProperty("user.dir") + '/csvdata').mkdir();

        path = System.getProperty("user.dir")   + '/csvdata';

        csvFile = new File(path+'/foo.csv')

        csvFile.write('abc,abbb,sds,sdsss,ssfffff\n123,,123,sddf,\r\n')

    }

    def cleanup() {

        csvFile.delete();
        new File(System.getProperty("user.dir") + '/csvdata').deleteDir();

    }



    def "Should Publish CSV"() {
        given:
            def task = Spy(PublishTask);
            task.csvFilesRelativePath = path.minus('foo.csv')
            def mockWebClient = Mock(SalesforceWebServiceClient);
            List<Map<String, Object>> result = new ArrayList<>()

        when:
            task.createWebClient() >> mockWebClient;
            mockWebClient.createObject(_,_)>> '123'
            mockWebClient.exportDataFromTable(_,_, _)>> result
            task.execute();

        then:
            assert task.publishId == '123';
    }
}
