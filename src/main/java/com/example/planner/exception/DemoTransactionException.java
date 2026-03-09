package com.example.planner.exception;

public class DemoTransactionException extends RuntimeException {

  public DemoTransactionException(String message) {
	super(message);
  }

  public DemoTransactionException(String message, Throwable cause) {
	super(message, cause);
  }
}