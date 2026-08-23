package com.youtube.analyser.youtubenalyser.exception;

public class InsufficientDataException extends RuntimeException {
    public InsufficientDataException(String message) {
        super(message);
    }
}