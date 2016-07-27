package com.github.mideo.salesforce

import spock.lang.Specification

class SalesforceConfigTest extends Specification {

    def "Should return config string"() {
        when:
            def config = new SalesforceConfig("abc")
                    .userName("sdsds")
                    .passwordAndToken("sdsds")
                    .apiVersion(40.0);

        then:
            assert config.user == 'sdsds'
            assert config.passwordAndToken == 'sdsds'
            assert config.version == '40.0'
    }
}
