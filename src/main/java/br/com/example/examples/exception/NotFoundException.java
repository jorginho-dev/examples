package br.com.example.examples.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends BusinessException{

    public NotFoundException(final String message) {
        super(HttpStatus.NOT_FOUND, "", message, "");
    }

}
