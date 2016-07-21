package com.github.mideo.http

import com.jayway.restassured.RestAssured
import com.jayway.restassured.specification.RequestSpecification

class HttpRequest {
    static RequestSpecification getSpecification() {
        return RestAssured.given();
    }
}
