package salesforce

import com.jayway.restassured.specification.RequestSpecification
import spock.lang.Specification


class HttpRequestSpecBuilderTest extends Specification {
    def "Should Return Request Specification"() {

        given:
            RequestSpecification requestSpecification
            HttpRequestSpecBuilder httpRequestSpecBuilder = new HttpRequestSpecBuilder();

        when:
            requestSpecification = httpRequestSpecBuilder.getRequestSpecification()

        then:
            assert requestSpecification != null
    }

    def "Should throw AssertionError for non 200 response"() {

        given:
            HttpRequestSpecBuilder httpRequestSpecBuilder = new HttpRequestSpecBuilder();

        when:
            httpRequestSpecBuilder.getRequestSpecification().get("http://foobar.com/ssdssdsd")

        then:
            thrown(AssertionError)
    }
}
