package com.github.mideo.salesforce.ant

import com.github.mideo.salesforce.SalesforceWebServiceClient
import spock.lang.Specification


class RetrieveTaskTest extends Specification {

    File configJsonFile
    def path
    def configFileName = 'testConf.json'

    def setup() {
        configJsonFile = new File(configFileName)
        configJsonFile.write('{"DataSource_Driver_Registry__c" : ["ClassName__c","Name","Name_Prefix__c"]}')
        path = configJsonFile.getCanonicalPath() - configFileName
    }

    def cleanup() {
        configJsonFile.delete()
        new File("${path}/DataSource_Driver_Registry__c.csv").delete()
    }

    def "test retrieve sObjects into csv"() {
        given:
            def task = Spy(RetrieveTask);
            task.csvFilesRelativePath = path
            task.configFileName = configFileName
            def mockWebClient = Mock(SalesforceWebServiceClient);
            List<Map<String, String>> retrieveResult = new ArrayList<>();
            Map<String, String> rowMap = new HashMap<>();
            rowMap.put("ClassName__c", "className1");
            rowMap.put("Name","Name1")
            rowMap.put("Name_Prefix__c","NamePrefix1")
            retrieveResult.add(rowMap)

        when:

            task.createWebClient() >> mockWebClient;
            mockWebClient.exportDataFromTable(_, _) >> retrieveResult
            task.execute();

        then:
            def resultCsv = new File("${path}/DataSource_Driver_Registry__c.csv")
            assert resultCsv.exists();
            def lines = resultCsv.readLines()
            assert 2 == lines.size()
            assert lines[0] ==  'ClassName__c,Name,Name_Prefix__c'
            assert lines[1] == 'className1,Name1,NamePrefix1'
        }
}
