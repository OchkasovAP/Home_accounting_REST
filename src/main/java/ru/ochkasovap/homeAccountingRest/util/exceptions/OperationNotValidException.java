package ru.ochkasovap.homeAccountingRest.util.exceptions;

import org.springframework.validation.BindingResult;

public class OperationNotValidException extends FieldsNotValidException{

	public OperationNotValidException(BindingResult bindingResult) {
		super(bindingResult);
	}
	
}