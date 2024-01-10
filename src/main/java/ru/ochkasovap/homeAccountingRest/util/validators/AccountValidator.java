package ru.ochkasovap.homeAccountingRest.util.validators;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ru.ochkasovap.homeAccountingRest.models.CashAccount;
import ru.ochkasovap.homeAccountingRest.models.User;
import ru.ochkasovap.homeAccountingRest.services.CashAccountsService;

@Component
public class AccountValidator implements Validator {
	@Autowired
	private CashAccountsService accountsService;

	@Override
	public boolean supports(Class<?> clazz) {
		return CashAccount.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		CashAccount account = (CashAccount) target;
		Optional<CashAccount> accountFromDB = accountsService.findByNameAndUser(account.getName(), account.getUser());
		if (accountFromDB.isPresent()) {
			if (accountFromDB.get().getId() != account.getId()) {
				errors.rejectValue("name", null, "Такой счет уже существует");
			}
		}
	}

}
