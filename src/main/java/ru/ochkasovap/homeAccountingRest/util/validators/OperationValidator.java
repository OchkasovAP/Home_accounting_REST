package ru.ochkasovap.homeAccountingRest.util.validators;

import java.util.Date;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ru.ochkasovap.homeAccountingRest.dto.OperationDTO;
import ru.ochkasovap.homeAccountingRest.models.Income;
import ru.ochkasovap.homeAccountingRest.models.Outcome;
import ru.ochkasovap.homeAccountingRest.util.DateUtil;
import ru.ochkasovap.homeAccountingRest.util.Operation;
@Component
public class OperationValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		return OperationDTO.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		OperationDTO operation = (OperationDTO) target;
		if(operation.getDate().getTime()>System.currentTimeMillis()) {
			errors.rejectValue("date", null, "Дата не может быть позже текущей");
		}
		
	}
	
}
