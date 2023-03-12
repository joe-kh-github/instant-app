package com.instantsystem.instantapp.config;

import com.instantsystem.instantapp.exceptions.InstantException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class AdviceController {

    @ExceptionHandler(InstantException.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleException(InstantException instantException) {
        Map<String, String> map = new HashMap<>();
        map.put("message", instantException.getMessage());
        map.put("code", String.valueOf(instantException.getHttpStatus().value()));
        return new ResponseEntity<>(map, HttpStatus.valueOf(instantException.getHttpStatus().value()));
    }
}
