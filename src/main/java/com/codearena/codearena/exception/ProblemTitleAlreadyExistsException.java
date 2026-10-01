package com.codearena.codearena.exception;

public class ProblemTitleAlreadyExistsException
        extends RuntimeException {

    public ProblemTitleAlreadyExistsException(String message) {
        super(message);
    }
}