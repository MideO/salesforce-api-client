package salesforce

import com.jayway.restassured.specification.RequestSpecification
import spock.lang.Specification


class HttpRequestSpecBuilderTest extends Specification {
    def "getRequestSpecification Should Return Request Specification"() {

        given:
            RequestSpecification requestSpecification
            HttpRequestSpecBuilder httpRequestSpecBuilder = new HttpRequestSpecBuilder();

        when:
            requestSpecification = httpRequestSpecBuilder.getRequestSpecification()

        then:
            assert requestSpecification != null


    }
}
