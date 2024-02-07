package ru.ochkasovap.homeAccountingRest.util.validators;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.ochkasovap.homeAccountingRest.models.Category;
import ru.ochkasovap.homeAccountingRest.models.Operation;
import ru.ochkasovap.homeAccountingRest.models.User;
import ru.ochkasovap.homeAccountingRest.services.CashAccountsService;
import ru.ochkasovap.homeAccountingRest.services.CategoriesService;
@Component
public class OperationValidator implements Validator{
	private final CashAccountsService accountService;
	private final CategoriesService categoriesService;
	
	@Autowired
	public OperationValidator(CashAccountsService accountService, CategoriesService categoriesService) {
		super();
		this.accountService = accountService;
		this.categoriesService = categoriesService;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return Operation.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Operation operation = (Operation) target;
		User user = operation.getUser();
		if(operation.getDate()!=null) {
			Calendar operationDate = Calendar.getInstance();
			operationDate.setTime(operation.getDate());
			long currentTime = System.currentTimeMillis()+operationDate.get(Calendar.HOUR)*60*60*1000;
			if(operation.getDate().getTime()>currentTime) {
				errors.rejectValue("date", null, "Дата не может быть позже текущей");
			}
		}
		String accountName = operation.getCashAccount().getName();
		if(!nullString(accountName)&&accountService.findByNameAndUser(accountName, user).isEmpty()) {
			errors.rejectValue("account", null, "У данного пользователя не существует счета с таким именем");
		}
		Category category = operation.getCategory();
		category.setUser(user);
		if(!nullString(category.getName())&&categoriesService.findInDB(category).isEmpty()) {
			errors.rejectValue("category", null, "У данного пользователя не существует категории с таким именем");
		}
	}
	private boolean nullString(String s) {
		return s==null||s.isBlank();
	}
}

