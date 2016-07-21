package com.github.mideo.http

import com.jayway.restassured.specification.RequestSpecification
import spock.lang.Specification


class HttpRequestTest extends Specification {
    def "Should Return Request Specification"() {

        given:
            RequestSpecification requestSpecification;

        when:
            requestSpecification = HttpRequest.getSpecification()

        then:
            assert requestSpecification != null
    }
}
