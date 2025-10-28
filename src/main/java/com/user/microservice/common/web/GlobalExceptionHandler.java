package com.user.microservice.common.web;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.user.microservice.common.errors.AlreadyExistsException;
import com.user.microservice.common.errors.BadRequestException;
import com.user.microservice.common.errors.NotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
  private Map<String,Object> body(HttpStatus s, String msg){
    return Map.of("status", s.value(), "error", s.getReasonPhrase(), "message", msg);
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<?> nf(NotFoundException ex){ return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body(HttpStatus.NOT_FOUND, ex.getMessage())); }

  @ExceptionHandler({AlreadyExistsException.class, BadRequestException.class, IllegalArgumentException.class})
  public ResponseEntity<?> bad(RuntimeException ex){ return ResponseEntity.badRequest().body(body(HttpStatus.BAD_REQUEST, ex.getMessage())); }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> any(Exception ex){ return ResponseEntity.status(500).body(body(HttpStatus.INTERNAL_SERVER_ERROR, "Error inesperado")); }
}