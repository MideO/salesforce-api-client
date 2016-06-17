package salesforce;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.specification.RequestSpecification;



class HttpRequestSpecBuilder {

    RequestSpecification getRequestSpecification() {
        return RestAssured
                .expect()
                .statusCode(200)
                .given();
    }


}
