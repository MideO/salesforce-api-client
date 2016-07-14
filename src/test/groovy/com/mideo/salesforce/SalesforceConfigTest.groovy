package com.mideo.salesforce

import spock.lang.Specification

class SalesforceConfigTest extends Specification {

    def "Should return config string"() {
        when:
            SalesforceConfig config = new SalesforceConfig("abc")
                    .clientId("wewew")
                    .clientSecret("dfdfd")
                    .userName("sdsds")
                    .password("sdsds")
                    .userToken("sdssd");

        then:
            assert config.toString() == "grant_type=password&client_id=wewew&client_secret=dfdfd&username=sdsds&password=sdsdssdssd";
    }
}
