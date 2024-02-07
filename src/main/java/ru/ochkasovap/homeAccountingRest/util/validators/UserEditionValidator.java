package ru.ochkasovap.homeAccountingRest.util.validators;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ru.ochkasovap.homeAccountingRest.dto.EditUserDTO;

import ru.ochkasovap.homeAccountingRest.models.User;
import ru.ochkasovap.homeAccountingRest.services.UserService;

@Component
public class UserEditionValidator implements Validator {
	private final PasswordEncoder encoder;
	private final UserService service;
	private EditUserDTO validUser;

	@Autowired
	public UserEditionValidator(PasswordEncoder encoder, UserService service) {
		super();
		this.encoder = encoder;
		this.service = service;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return EditUserDTO.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		validUser = (EditUserDTO) target;
		passwordValidate(errors);
		if (loginExists()) {
			errors.rejectValue("login", null, "Пользователь с таким именем уже существует");
		}
	}
	private void passwordValidate(Errors errors) {
		if(!fieldIsEmpty("password")) {
			User user = service.findById(validUser.getId());
			if (!encoder.matches(validUser.getPassword(), user.getPassword())) {
				errors.rejectValue("password", null, "Неверный текущий пароль");
			}
			for(String fieldName:new String[] {"newPassword","repeatedNewPassword"}) {
				if(fieldIsEmpty(fieldName)){
					errors.rejectValue(fieldName, null, "Поле не должно быть пустым");
				}
			}
			if (nonCorrectRepeatPassword()) {
				errors.rejectValue("repeatedNewPassword", null, "Неверно повторен пароль");
			}
		}
	}
	private boolean loginExists() {
		Optional<User> userFromDB = service.findByLogin(validUser.getLogin());
		return userFromDB.isPresent() && userFromDB.get().getId() != validUser.getId();
	}
	
	private boolean fieldIsEmpty(String fieldName) {
		try {
			String getter = convertFieldNameToGetterName(fieldName);
			String fieldValue = (String) EditUserDTO.class.getDeclaredMethod(getter).invoke(validUser);
			return fieldValue==null||fieldValue.isBlank();
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	private String convertFieldNameToGetterName(String fieldName) {
		StringBuilder builder = new StringBuilder(fieldName);
		builder.setCharAt(0, fieldName.toUpperCase().charAt(0));
		builder.insert(0, "get");
		return builder.toString();
	}
	private boolean nonCorrectRepeatPassword() {
		return !fieldIsEmpty("newPassword")&&!fieldIsEmpty("repeatedNewPassword")
				&& !validUser.getNewPassword().equals(validUser.getRepeatedNewPassword());
	}
}
