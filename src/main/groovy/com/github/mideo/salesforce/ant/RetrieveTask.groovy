package com.github.mideo.salesforce.ant

import com.github.mideo.http.HttpRequest
import com.github.mideo.salesforce.SalesforceConfig
import com.github.mideo.salesforce.SalesforceConnectionClient
import com.github.mideo.salesforce.SalesforceWebServiceClient
import groovy.json.JsonException
import groovy.json.JsonSlurper
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter

class RetrieveTask extends SalesforceTask {

    private SalesforceWebServiceClient webClient;
    private SalesforceConfig config;
    private SalesforceConnectionClient connectionClient
    JsonSlurper slurper = new JsonSlurper()

    SalesforceWebServiceClient createWebClient() {
        config = new SalesforceConfig(serverUrl)
                .userName(userName)
                .passwordAndToken(password);
        connectionClient = new SalesforceConnectionClient(config, HttpRequest.getSpecification());
        new SalesforceWebServiceClient(connectionClient);

    }

    public void execute() {
        webClient = createWebClient();
        csvFilesRelativePath = csvFilesRelativePath.endsWith('/') ? csvFilesRelativePath : csvFilesRelativePath + '/'

        def configJson
        try {
            new File("${csvFilesRelativePath}${configFileName}").withReader {
                line -> configJson = slurper.parse(line)
            }
        }catch(JsonException jex){
            throw new Exception("Unable to read file\n"+ jex.printStackTrace())
        }

        configJson.each {
            File csvFileToWrite = new File("${csvFilesRelativePath}${it.key}.csv");
            if (csvFileToWrite.exists()) {
                csvFileToWrite.delete();
            }

            List<Map<String, String>> exportedData = webClient.exportDataFromTable(it.key, it.value);
            List headersFromConfigFile = it.value
            println "Writing file to ${csvFileToWrite.getName()} to ${csvFilesRelativePath}"
            writeRecordsToCSV(csvFileToWrite, headersFromConfigFile, exportedData)

        }

    }

    private void writeRecordsToCSV(File csvFile, List headers, List<Map<String, String>> rows) {

        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator('\n');
        CSVPrinter csvFilePrinter = null;

        def fileWriter = new FileWriter(csvFile)
        try {
            csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);

            csvFilePrinter.printRecord(headers);
            def currentRowInResponseList;

            rows.each {
                row ->
                    currentRowInResponseList = new ArrayList()
                    headers.each { header -> currentRowInResponseList.add(row.get(header)); }
                    csvFilePrinter.printRecord(currentRowInResponseList);
            }
        } finally {
            fileWriter.flush();
            fileWriter.close();
            csvFilePrinter.close();
        }
    }
}
