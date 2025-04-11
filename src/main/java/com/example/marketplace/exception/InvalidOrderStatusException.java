package com.example.marketplace.exception;

public class InvalidOrderStatusException extends RuntimeException {
  public InvalidOrderStatusException(String message) {
    super(message);
  }
}
