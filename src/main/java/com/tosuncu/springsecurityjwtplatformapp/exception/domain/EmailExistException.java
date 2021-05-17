package com.tosuncu.springsecurityjwtplatformapp.exception.domain;

public class EmailExistException extends Exception {
    public EmailExistException(String message) {
        super(message);
    }
}
