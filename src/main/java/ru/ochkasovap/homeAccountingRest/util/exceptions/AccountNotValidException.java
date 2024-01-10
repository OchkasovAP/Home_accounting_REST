package ru.ochkasovap.homeAccountingRest.util.exceptions;

import org.springframework.validation.BindingResult;

public class AccountNotValidException extends FieldsNotValidException{

	public AccountNotValidException(BindingResult bindingResult) {
		super(bindingResult);
	}
	
}
