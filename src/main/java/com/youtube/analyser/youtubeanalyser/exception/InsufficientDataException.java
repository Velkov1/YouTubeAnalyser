package com.youtube.analyser.youtubeanalyser.exception;

public class InsufficientDataException extends RuntimeException {
    public InsufficientDataException(String message) {
        super(message);
    }
}