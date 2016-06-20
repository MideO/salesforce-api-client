package com.mideo.http;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.specification.RequestSpecification;



public class HttpRequestSpecificationBuilder {

    public RequestSpecification build() {
        return RestAssured
                .expect()
                .statusCode(200)
                .given();
    }


}
