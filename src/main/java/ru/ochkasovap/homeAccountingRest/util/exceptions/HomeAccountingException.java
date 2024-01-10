package ru.ochkasovap.homeAccountingRest.util.exceptions;

public class HomeAccountingException extends RuntimeException{

	public HomeAccountingException() {
		super();
	}

	public HomeAccountingException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public HomeAccountingException(String message, Throwable cause) {
		super(message, cause);
	}

	public HomeAccountingException(String message) {
		super(message);
	}

	public HomeAccountingException(Throwable cause) {
		super(cause);
	}
	
}
