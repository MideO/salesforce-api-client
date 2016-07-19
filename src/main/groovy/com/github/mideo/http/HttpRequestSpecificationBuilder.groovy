package com.github.mideo.http

import com.jayway.restassured.RestAssured
import com.jayway.restassured.specification.RequestSpecification


class HttpRequestSpecificationBuilder {
    static RequestSpecification build() {
        return RestAssured
                .expect()
                .statusCode(200)
                .given();
    }
}
