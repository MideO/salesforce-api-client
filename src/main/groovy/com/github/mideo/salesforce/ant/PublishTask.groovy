package com.github.mideo.salesforce.ant

import com.github.mideo.salesforce.PublishResult
import com.github.mideo.salesforce.SalesforceConfig
import com.github.mideo.salesforce.SalesforceConnectionClient
import com.github.mideo.salesforce.SalesforceWebServiceClient
import groovy.io.FileType
import groovy.json.JsonOutput

class PublishTask extends SalesforceTask{

    private SalesforceWebServiceClient webClient;
    private PublishResult result
    private SalesforceConfig config;
    private SalesforceConnectionClient connectionClient

    SalesforceWebServiceClient createWebClient(){
        config = new SalesforceConfig(serverUrl)
                .userName(userName)
                .passwordAndToken(password);
        connectionClient = new SalesforceConnectionClient(config);
        new SalesforceWebServiceClient(connectionClient);

    }
    public void execute(){
        webClient = createWebClient();
        File directory = new File(csvFilesRelativePath)
        if (!directory.exists()){
            println "Directory not found, ensure the directory exists `${csvFilesRelativePath}`"
            return;
        }
        new File(csvFilesRelativePath).eachFileRecurse (FileType.FILES) {

           file -> if(file.name.endsWith('.csv') ){
               def sObjectName = file.name - '.csv'
               println "Publishing custom settings for ${sObjectName}"
               webClient.exportDataFromTable(sObjectName, ['Id']).each {
                   webClient.deleteObject(it['Id']);
               }

               result = webClient.publishCsvToTable(new FileInputStream(file), sObjectName)

               if (!result.isPublished()){
                   throw new Exception(JsonOutput.prettyPrint(JsonOutput.toJson(result)));
               }

           }
        }
        println "Published custom settings completed"


    }
}


