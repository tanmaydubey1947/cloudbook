package com.cloudbook.common.exception;

import com.cloudbook.common.exception.payload.ExceptionMessage;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Hidden
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionMessage> handleRuntimeException(Exception ex) {
        log.error("Unexpected Exception Occurred: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionMessage.builder()
                        .msg(ex.getMessage())
                        .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .build());
    }

}