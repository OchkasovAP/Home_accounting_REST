package ru.ochkasovap.homeAccountingRest.util.exceptions;

import org.springframework.validation.BindingResult;

public class CategoryNotValidException extends FieldsNotValidException{

	public CategoryNotValidException(BindingResult bindingResult) {
		super(bindingResult);
	}

}
