package http

import com.jayway.restassured.specification.RequestSpecification
import spock.lang.Specification


class HttpRequestSpecificationBuilderTest extends Specification {
    def "Should Return Request Specification"() {

        given:
            RequestSpecification requestSpecification
            HttpRequestSpecificationBuilder httpRequestSpecBuilder = new HttpRequestSpecificationBuilder();

        when:
            requestSpecification = httpRequestSpecBuilder.build()

        then:
            assert requestSpecification != null
    }

    def "Should throw AssertionError for non 200 response"() {

        given:
            HttpRequestSpecificationBuilder httpRequestSpecBuilder = new HttpRequestSpecificationBuilder();

        when:
            httpRequestSpecBuilder.build().get("https://test.salesforce.com/ssdssdsd")

        then:
            thrown(AssertionError)
    }
}
