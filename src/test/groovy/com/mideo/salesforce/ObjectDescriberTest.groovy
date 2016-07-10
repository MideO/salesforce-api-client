package com.mideo.salesforce

import com.sforce.soap.partner.DescribeSObjectResult
import com.sforce.soap.partner.PartnerConnection
import spock.lang.Specification

import com.sforce.soap.partner.Field

class ObjectDescriberTest extends Specification {

    def "Should Set Salesforce Client"() {
        given:
            ObjectDescriber describer = new ObjectDescriber();
            SalesforceConnectionClient mockConnectionClient = Mock(SalesforceConnectionClient);

        when:
            describer.withSalesforceClient(mockConnectionClient);

        then:
            describer.salesforceConnectionClient == mockConnectionClient;
    }

    def "Should Get Data Columns"() {
        given:
            String tableName = "Rubarb";
            ObjectDescriber describer = new ObjectDescriber();
            SalesforceConnectionClient mockConnectionClient = Mock(SalesforceConnectionClient);
            PartnerConnection mockPartnerConnection = Mock(PartnerConnection);
            DescribeSObjectResult mockDescribeSObjectResult = Mock(DescribeSObjectResult);
            Field mockField = Mock(Field);
            Field[] mockFields = [mockField];

        when:
            mockConnectionClient.getSalesForceWebServicePartnerConnection() >> mockPartnerConnection;
            mockPartnerConnection.describeSObject(tableName) >> mockDescribeSObjectResult;
            mockDescribeSObjectResult.getFields() >> mockFields;
            mockField.getName() >> "fruit";
            List<String> resultName = describer.withSalesforceClient(mockConnectionClient)
                    .getDataColumns(tableName);

        then:
            assert resultName.size() == 1;
            assert resultName.contains("fruit");
    }
}
