package com.reece.exception;

public class AddressBookAPIException extends RuntimeException {
    public AddressBookAPIException(String message) {
        super(message);
    }
}
