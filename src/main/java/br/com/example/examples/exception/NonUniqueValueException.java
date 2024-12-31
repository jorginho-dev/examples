package br.com.example.examples.exception;

import org.springframework.http.HttpStatus;

public class NonUniqueValueException extends BusinessException{

    public NonUniqueValueException(final String message) {
        super(HttpStatus.CONFLICT, "", message, "");
    }
}
