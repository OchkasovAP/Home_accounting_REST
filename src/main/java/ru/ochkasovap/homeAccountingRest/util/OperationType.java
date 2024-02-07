package ru.ochkasovap.homeAccountingRest.util;

import java.lang.reflect.InvocationTargetException;

import ru.ochkasovap.homeAccountingRest.models.Category;
import ru.ochkasovap.homeAccountingRest.models.Income;
import ru.ochkasovap.homeAccountingRest.models.IncomeCategory;
import ru.ochkasovap.homeAccountingRest.models.Operation;
import ru.ochkasovap.homeAccountingRest.models.Outcome;
import ru.ochkasovap.homeAccountingRest.models.OutcomeCategory;

public enum OperationType {
	INCOME(Income.class, IncomeCategory.class), OUTCOME(Outcome.class, OutcomeCategory.class);

	private final Class<? extends Operation> operationClass;
	private final Class<? extends Category> categoryClass;

	private OperationType(Class<? extends Operation> operationClass, Class<? extends Category> categoryClass) {
		this.operationClass = operationClass;
		this.categoryClass = categoryClass;
	}

	@SuppressWarnings("unchecked")
	public <T extends Operation> Class<T> getOperationClass() {
		return (Class<T>) operationClass;
	}

	@SuppressWarnings("unchecked")
	public <T extends Category> Class<T> getCategoryClass() {
		return (Class<T>) categoryClass;
	}

	public Category newCategory(String categoryName) {
		try {
			return categoryClass.getConstructor(String.class).newInstance(categoryName);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	public Operation newEmptyOperation() {
		try {
			return operationClass.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}
}
