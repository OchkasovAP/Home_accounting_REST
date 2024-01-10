package ru.ochkasovap.homeAccountingRest.util.exceptions;

import org.springframework.validation.BindingResult;

public class UserNotValidException extends FieldsNotValidException{

	public UserNotValidException(BindingResult bindingResult) {
		super(bindingResult);
	}
	
}
