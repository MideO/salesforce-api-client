package com.github.mideo.salesforce;

import com.sforce.async.AsyncApiException
import com.sforce.async.BulkConnection;
import org.apache.commons.csv.CSVFormat;



class DataFetcher {
    BulkConnection bulkConnection;

    List<Map<String,String>> fetchData(String jobInfoId, String batchinfoId) throws AsyncApiException, IOException {
        def queryResultList =
                bulkConnection.getQueryResultList(jobInfoId, batchinfoId)

        def inputStream = bulkConnection
                .getQueryResultStream(jobInfoId, batchinfoId, queryResultList.getResult()[0])

        return CSVFormat.RFC4180.withFirstRecordAsHeader().parse(new InputStreamReader(inputStream)).collect {
            it.toMap();
        }
    }


}
