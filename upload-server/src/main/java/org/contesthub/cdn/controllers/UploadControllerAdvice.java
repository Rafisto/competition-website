package org.contesthub.cdn.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class UploadControllerAdvice{
    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<?> handleFileNotFoundException(FileNotFoundException e, HttpServletRequest request) {
        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_NOT_FOUND);
        body.put("error", "Not Found");
        body.put("message", e.getMessage());
        body.put("path", request.getRequestURI());
        return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body(body);
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<?> handleFileUploadException(FileUploadException e, HttpServletRequest request) {
        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_BAD_REQUEST);
        body.put("error", "Bad Request");
        body.put("message", e.getMessage());
        body.put("path", request.getRequestURI());
        return ResponseEntity.badRequest().body(body);
    }
}
