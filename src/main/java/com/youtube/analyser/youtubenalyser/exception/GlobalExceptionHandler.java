package com.youtube.analyser.youtubenalyser.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({NoSuchVideoException.class, NoSuchChannelException.class})
    public ResponseEntity<String> handleNotFound(RuntimeException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler({InvalidRegionException.class, InvalidCategoryIdException.class, IllegalArgumentException.class, InsufficientDataException.class})
    public ResponseEntity<String> handleBadRequest(RuntimeException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<String> handleYouTubeApiError(RestClientException e){
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("The request to the YouTube API failed. Check the API key and the daily quota.");
    }
}
