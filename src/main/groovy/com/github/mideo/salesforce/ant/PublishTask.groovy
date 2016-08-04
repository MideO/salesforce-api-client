package com.github.mideo.salesforce.ant

import com.github.mideo.salesforce.PublishResult
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
                    webClient.executeSoqlQuery("SELECT ID FROM ${sObjectName}")
                    webClient.exportDataFromTable(sObjectName, ['Id']).each {
                        try {
                            webClient.deleteObject(it['Id']);
                        } catch (ConnectionException connectionException) {
                            throw new Exception(
                                    "Failed to delete \nObject: ${sObjectName}\nCode: ${connectionException.getMessage()}"
                            );
                        }
                    }

                    try {
                        def lines = file.readLines()
                        def keys = lines[0].split(',')
                        def map = [:]
                        if (lines.size() > 1) {
                            lines[1..-1].collect { line ->
                                List vals = line.split(',')
                                if (line.endsWith(',')) {
                                    vals.add('')
                                }
                                for (int i = 0; i < keys.size(); i++) {
                                    map[keys[i]] = vals[i]
                                }
                                publishId = webClient.createObject(sObjectName, map)
                            }
                        }

                    } catch (Exception ex) {
                        throw new Exception(
                                "Failed to publish CSV: \nMessage: ${ex}"
                        );
                    }
                }
        }
        println "Published custom settings completed"
    }
}


