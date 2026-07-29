package com.example.api.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Log4j2
@RestControllerAdvice
public class RestControllerExceptionAdvice {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage()));
  }


  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {

    log.error("예상하지 못한 서버 오류가 발생했습니다.", e);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse("서버 에러"));

  }

  @ExceptionHandler
  public ResponseEntity handleException(BadRequestException e) {
    return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    // 400
  }

  @ExceptionHandler
  public ResponseEntity handleException(UnauthenticatedException e) {
    return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.UNAUTHORIZED);
    // 401
  }

  @ExceptionHandler
  public ResponseEntity handleException(ForbiddenException e) {
    return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.FORBIDDEN);
    // 403
  }

  @ExceptionHandler
  public ResponseEntity handleException(NoResourceFoundException e) {
    return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
    // 404
  }

  static class ErrorResponse {
    String message;

    ErrorResponse(String message) {
      this.message = message;
    }

    public String getMessage() {
      return message;
    }
  }

  @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "잘못된 요청 오류")
  private class BadRequestException extends RuntimeException {
  }

  @ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "잘못된 요청 오류")
  private class UnauthenticatedException extends RuntimeException {
  }

  @ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "잘못된 요청 오류")
  private class ForbiddenException extends RuntimeException {
  }
}