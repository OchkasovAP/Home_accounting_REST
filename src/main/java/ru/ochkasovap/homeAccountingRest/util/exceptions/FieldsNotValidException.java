package ru.ochkasovap.homeAccountingRest.util.exceptions;

import org.springframework.validation.BindingResult;

public class FieldsNotValidException extends HomeAccountingException{

	public FieldsNotValidException(BindingResult bindingResult) {
		super(getValidExceptionMessage(bindingResult));
	}
	private static String getValidExceptionMessage(BindingResult bindingResult) {
		StringBuilder stringBuilder = new StringBuilder();
		bindingResult.getFieldErrors().stream()
			.forEach(f -> stringBuilder
					.append("Field - ")
					.append(f.getField())
					.append(", error - ")
					.append(f.getDefaultMessage())
					.append(";"));
		return stringBuilder.toString();
	}
}
