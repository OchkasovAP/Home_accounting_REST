package ru.ochkasovap.homeAccountingRest.util.validators;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ru.ochkasovap.homeAccountingRest.dto.RegistrationDTO;
import ru.ochkasovap.homeAccountingRest.models.User;
import ru.ochkasovap.homeAccountingRest.services.UserService;
	
@Component
public class UserCreationValidator implements Validator{
	private final UserService service;
	
	private RegistrationDTO validUser;	

	public UserCreationValidator(UserService service) {
		super();
		this.service = service;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return RegistrationDTO.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		validUser = (RegistrationDTO) target;
		if(loginExists()) {
			errors.rejectValue("login", null, "Пользователь с таким именем уже существует");
		}
		if(validUser.getPassword()!=null && nonCorrectRepeatPassword()) {
			errors.rejectValue("repeatedNewPassword", null, "Неверно повторен пароль");
		}
	}
	
	private boolean loginExists() {
		Optional<User> userFromDB = service.findByLogin(validUser.getLogin());
		return userFromDB.isPresent(); 	
	}
	private boolean nonCorrectRepeatPassword() {
		return !validUser.getPassword().equals(validUser.getRepeatedNewPassword());
	}
	
}
