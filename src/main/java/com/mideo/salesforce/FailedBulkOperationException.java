package com.mideo.salesforce;


public class FailedBulkOperationException extends Exception {
    FailedBulkOperationException(String message) {
        super(message);
    }
}
