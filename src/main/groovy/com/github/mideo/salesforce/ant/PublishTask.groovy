package com.github.mideo.salesforce.ant

import com.github.mideo.salesforce.SalesforceConfig
import com.github.mideo.salesforce.SalesforceConnectionClient
import com.github.mideo.salesforce.SalesforceWebServiceClient
import com.sforce.ws.ConnectionException
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser


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

        Map<String, List<List<Map>>> csvDataMap = new File(csvFilesRelativePath)
                .listFiles()
                .findAll { it.name.endsWith('.csv') }
                .collectEntries {
            [
                    it.name - '.csv',
                    it.withReader { reader -> new CSVParser(reader, CSVFormat.DEFAULT.withHeader()).collect { record -> record.toMap() } }
            ]
        }

        csvDataMap.each { sObjectName, entryList ->
                println "Publishing custom settings for ${sObjectName}"
                try {
                    createRecordsByDeletingIndividualEntry(sObjectName, entryList as List<Map>)

                } catch (Exception ignored) {
                    createRecordsByTruncatingTable(sObjectName, entryList as List<Map>)
                }
        }
        println "Published custom settings completed"
    }

    private void createRecordsByDeletingIndividualEntry(String sObjectName, List<Map> entryMapList) {
        entryMapList.collect {
            def searchMap = [:]
            if(it.containsKey("Name")){
                searchMap["Name"] = it["Name"]
            } else {
                searchMap[it.keySet()[0]] = it[it.keySet()[0]]
            }
            List<Map<String, Object>> result = webClient.exportDataFromTable(sObjectName, ['Id'], searchMap)
            if (result.size() > 0) {
                result.each {
                    webClient.deleteObject(it['Id'] as String)
                }
            }
            publishId = webClient.createObject(sObjectName, it)
        }

    }


    private void createRecordsByTruncatingTable(String sObjectName, List<Map> entryMapList) {
        webClient.exportDataFromTable(sObjectName, ['Id']).each {
            try {
                webClient.deleteObject(it['Id']);
            }
            catch (ConnectionException connectionException) {
                throw new Exception("Failed to delete \nObject: ${sObjectName}\nCode: ${connectionException.getMessage()}");
            }
        }
        entryMapList.collect {
            publishId = webClient.createObject(sObjectName, it)
        }


    }
}


