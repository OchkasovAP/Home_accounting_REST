package ru.ochkasovap.homeAccountingRest.util.validators;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ru.ochkasovap.homeAccountingRest.models.IncomeCategory;
import ru.ochkasovap.homeAccountingRest.models.OutcomeCategory;
import ru.ochkasovap.homeAccountingRest.services.CategoriesService;
import ru.ochkasovap.homeAccountingRest.util.Category;

@Component
public class CategoryValidator implements Validator{
	@Autowired
	private CategoriesService categoriesService;

	@Override
	public boolean supports(Class<?> clazz) {
		return IncomeCategory.class.equals(clazz)||OutcomeCategory.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Category category = (Category) target;
		Optional<Category> categoryInDB = categoriesService.findInDB(category);
		if(categoryInDB.isPresent()) {
			if(categoryInDB.get().getId()!=category.getId()) {
				errors.rejectValue("name", null, "Категория с таким именем уже существует");
			}
		}
		
	}
	
	
}
