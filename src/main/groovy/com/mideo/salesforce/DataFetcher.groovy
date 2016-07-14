package com.mideo.salesforce;

import com.sforce.async.AsyncApiException;
import org.apache.commons.csv.CSVFormat;



class DataFetcher {
    SalesforceConnectionClient salesforceConnectionClient;

    List<Map<String,String>> fetchData(String jobInfoId, String batchinfoId) throws AsyncApiException, IOException {
        def queryResultList = salesforceConnectionClient
                .getSalesForceWebServiceBulkConnection()
                .getQueryResultList(jobInfoId, batchinfoId)

        def inputStream = salesforceConnectionClient
                .getSalesForceWebServiceBulkConnection()
                .getQueryResultStream(jobInfoId, batchinfoId, queryResultList.getResult()[0])

        return CSVFormat.RFC4180.withFirstRecordAsHeader().parse(new InputStreamReader(inputStream)).collect {
            it.toMap();
        }
    }


}
