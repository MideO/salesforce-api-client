package com.mideo.salesforce;

import com.sforce.async.AsyncApiException;
import com.sforce.async.QueryResultList;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;



import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


class DataFetcher {
    private SalesforceConnectionClient salesforceConnectionClient;

    DataFetcher withSalesforceClient(SalesforceConnectionClient salesforceConnectionClient) {
        this.salesforceConnectionClient = salesforceConnectionClient;
        return this;
    }

    List<Map<String,String>> fetchData(String jobInfoId, String batchinfoId) throws AsyncApiException, IOException {

        List<Map<String, String>> rows = new ArrayList<>();
        QueryResultList queryResultList = salesforceConnectionClient
                .getSalesForceWebServiceBulkConnection()
                .getQueryResultList(jobInfoId, batchinfoId);

        InputStream inputStream = salesforceConnectionClient
                    .getSalesForceWebServiceBulkConnection()
                    .getQueryResultStream(jobInfoId, batchinfoId, queryResultList.getResult()[0]);

        CSVParser parser =  CSVFormat.RFC4180.withFirstRecordAsHeader().parse(new InputStreamReader(inputStream));

        for (CSVRecord csvRecord : parser) {
            rows.add(csvRecord.toMap());
        }

        return rows;
    }


}
