package ru.ochkasovap.homeAccountingRest.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import ru.ochkasovap.homeAccountingRest.models.Income;
import ru.ochkasovap.homeAccountingRest.models.IncomeCategory;
import ru.ochkasovap.homeAccountingRest.models.Outcome;
import ru.ochkasovap.homeAccountingRest.models.OutcomeCategory;

import static ru.ochkasovap.homeAccountingRest.util.OperationType.*;

class OperationTypeTest {

	@Test
	void getTypeFromName() {
		for(String typeName:new String[] {"outcome","fmdjskgnfsjk",null}) {
			assertEquals(OUTCOME, OperationType.getTypeFromName(typeName));
		}
		assertEquals(INCOME, OperationType.getTypeFromName("income"));
	}
	
	@Test
	void newEmptyOperation() {
		assertEquals(Income.class, INCOME.newEmptyOperation().getClass());
		assertEquals(Outcome.class, OUTCOME.newEmptyOperation().getClass());
	}
	
	@Test
	void newCategory() {
		Category outcomeCategory = OUTCOME.newCategory("OUTCOME");
		Category incomeCategory = INCOME.newCategory("INCOME");
		assertEquals(OutcomeCategory.class, outcomeCategory.getClass());
		assertEquals(IncomeCategory.class, incomeCategory.getClass());
		assertEquals("OUTCOME", outcomeCategory.getName());
		assertEquals("INCOME", incomeCategory.getName());
	}

}
