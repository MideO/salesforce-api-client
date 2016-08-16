package com.github.mideo.salesforce.ant

import com.github.mideo.salesforce.SalesforceConfig
import com.github.mideo.salesforce.SalesforceConnectionClient
import com.github.mideo.salesforce.SalesforceWebServiceClient
import com.sforce.ws.ConnectionException
import groovy.io.FileType


class PublishTask extends SalesforceTask {

    private SalesforceWebServiceClient webClient;
    String publishId;
    private SalesforceConfig config;
    private SalesforceConnectionClient connectionClient

    SalesforceWebServiceClient createWebClient() {
        config = new SalesforceConfig(serverUrl)
                .userName(userName)
                .passwordAndToken(password);
        connectionClient = new SalesforceConnectionClient(config);
        new SalesforceWebServiceClient(connectionClient);

    }

    public void execute() {
        webClient = createWebClient();
        File directory = new File(csvFilesRelativePath)
        if (!directory.exists()) {
            println "Directory not found, ensure the directory exists `${csvFilesRelativePath}`"
            return;
        }

        new File(csvFilesRelativePath).eachFileRecurse(FileType.FILES) {

            file ->
                if (file.name.endsWith('.csv')) {
                    def sObjectName = file.name - '.csv'
                    println "Publishing custom settings for ${sObjectName}"
                    try {
                        def lines = file.readLines()
                        def keys = lines[0].split(',')
                        def dataMap = [:]
                        if (lines.size() > 1) {
                            lines[1..-1].collect { line ->
                                List vals = line.split(',')
                                if (line.endsWith(',')) {
                                    vals.add('')
                                }
                                for (int i = 0; i < keys.size(); i++) {
                                    dataMap[keys[i]] = vals[i]
                                }

                                def searchMap = [:]
                                searchMap[dataMap.keySet()[0]] = dataMap[dataMap.keySet()[0]]
                                List<Map<String, Object>> result = webClient.exportDataFromTable(sObjectName,['Id'], searchMap)
                                if (result.size() > 0){
                                    result.each {
                                        webClient.deleteObject(it['Id'] as String)
                                    }
                                }
                                    publishId = webClient.createObject(sObjectName, dataMap)


                            }
                        }

                    } catch (Exception ignored) {
                        webClient.exportDataFromTable(sObjectName, ['Id']).each {
                            try {
                                webClient.deleteObject(it['Id']);
                            } catch (ConnectionException connectionException) {
                                throw new Exception(
                                        "Failed to delete \nObject: ${sObjectName}\nCode: ${connectionException.getMessage()}"
                                );
                            }
                        }
                        def lines = file.readLines()
                        def keys = lines[0].split(',')
                        def dataMap = [:]
                        if (lines.size() > 1) {
                            lines[1..-1].collect { line ->
                                List vals = line.split(',')
                                if (line.endsWith(',')) {
                                    vals.add('')
                                }
                                for (int i = 0; i < keys.size(); i++) {
                                    dataMap[keys[i]] = vals[i]
                                }
                                publishId = webClient.createObject(sObjectName, dataMap)

                            }
                        }
                    }
                }
        }
        println "Published custom settings completed"
    }
}


