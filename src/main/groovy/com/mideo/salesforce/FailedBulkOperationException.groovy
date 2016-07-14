package com.mideo.salesforce

class FailedBulkOperationException extends Exception {
    FailedBulkOperationException(String message) {
        super(message);
    }
}

