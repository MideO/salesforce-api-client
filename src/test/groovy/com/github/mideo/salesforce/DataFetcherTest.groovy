package com.github.mideo.salesforce

import com.sforce.async.BulkConnection

import com.sforce.async.QueryResultList
import spock.lang.Specification


class DataFetcherTest extends Specification {

    def "Should fetch Data from salesforce Table"() {
        given:
            def jobInfoId = '123';
            def batchinfoId = 'abc';
            def resultId= 'z1x';

            def mockBulkConnection = Mock(BulkConnection);
            def dataFetcher = new DataFetcher(bulkConnection: mockBulkConnection);
            def mockQueryResultList =  Mock(QueryResultList);
            def inputStream = new ByteArrayInputStream('"carbs","protein"\r\n"bread","chicken"\r\n"pasta","eggs"'.getBytes());


        when:
            mockBulkConnection.getQueryResultList(jobInfoId, batchinfoId) >> mockQueryResultList;
            mockQueryResultList.getResult() >> [resultId];
            mockBulkConnection.getQueryResultStream(jobInfoId, batchinfoId, resultId) >> inputStream;
            List<Map<String,String>> result = dataFetcher.fetchData(jobInfoId, batchinfoId);


        then:
            assert result.size() == 2;
            assert result[0].get("carbs") == "bread";
            assert result[0].get("protein") == "chicken";
    }


}
