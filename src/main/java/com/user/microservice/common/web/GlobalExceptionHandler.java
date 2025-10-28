// common/web/GlobalExceptionHandler.java
package com.user.microservice.common.web;

import com.user.microservice.common.errors.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

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