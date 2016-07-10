package com.mideo.salesforce

import com.sforce.async.BulkConnection

import com.sforce.async.QueryResultList
import org.apache.commons.csv.CSVParser
import spock.lang.Specification


class DataFetcherTest extends Specification {
    def "Should Set Salesforce Client"() {
        given:
            DataFetcher dataFetcher = new DataFetcher();
            SalesforceConnectionClient mockConnectionClient = Mock(SalesforceConnectionClient);
        when:
            dataFetcher.withSalesforceClient(mockConnectionClient);
        then:
            assert dataFetcher.salesforceConnectionClient == mockConnectionClient;
    }


    def "Should fetch Data from salesforce Table"() {
        given:
            String jobInfoId = '123';
            String batchinfoId = 'abc';
            String resultId= 'z1x';
            DataFetcher dataFetcher = new DataFetcher();

            SalesforceConnectionClient mockConnectionClient = Mock(SalesforceConnectionClient);
            BulkConnection mockBulkConnection = Mock(BulkConnection);
            QueryResultList mockQueryResultList =  Mock(QueryResultList);

            InputStream inputStream = new ByteArrayInputStream('"carbs","protein"\r\n"bread","chicken"\r\n"pasta","eggs"'.getBytes());


        when:
            mockConnectionClient.getSalesForceWebServiceBulkConnection() >> mockBulkConnection;
            mockBulkConnection.getQueryResultList(jobInfoId, batchinfoId) >> mockQueryResultList;
            mockQueryResultList.getResult() >> [resultId];
            mockBulkConnection.getQueryResultStream(jobInfoId, batchinfoId, resultId) >> inputStream;
            List<Map<String,String>> result = dataFetcher.withSalesforceClient(mockConnectionClient).fetchData(jobInfoId, batchinfoId);


        then:
            assert result.size() == 2;
            assert result[0].get("carbs") == "bread";
            assert result[0].get("protein") == "chicken";
    }


}