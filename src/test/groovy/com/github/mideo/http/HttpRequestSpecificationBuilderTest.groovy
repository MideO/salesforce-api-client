package com.github.mideo.http

import com.jayway.restassured.specification.RequestSpecification
import spock.lang.Specification


class HttpRequestSpecificationBuilderTest extends Specification {
    def "Should Return Request Specification"() {

        given:
            RequestSpecification requestSpecification;

        when:
            requestSpecification = HttpRequestSpecificationBuilder.build()

        then:
            assert requestSpecification != null
    }

    def "Should throw AssertionError for non 200 response"() {
        when:
            HttpRequestSpecificationBuilder.build().get("https://test.salesforce.com/ssdssdsd")

        then:
            thrown(AssertionError)
    }
}
