package salesforce;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.filter.log.RequestLoggingFilter;
import com.jayway.restassured.filter.log.ResponseLoggingFilter;
import com.jayway.restassured.specification.RequestSpecification;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;


class HttpRequestSpecBuilder {

    private static ByteArrayOutputStream byteArrayOutputStreamResponse = new ByteArrayOutputStream();
    private static ByteArrayOutputStream byteArrayOutputStreamRequest = new ByteArrayOutputStream();
    private static PrintStream printStreamResponse = new PrintStream(byteArrayOutputStreamResponse);
    private static PrintStream printStreamRequest = new PrintStream(byteArrayOutputStreamRequest);


    RequestSpecification getRequestSpecification() {

        return RestAssured
                .given().log().all()
                .filter(new RequestLoggingFilter(printStreamRequest))
                .filter(new ResponseLoggingFilter(printStreamResponse))
                .expect().log().all().given();
    }

}
