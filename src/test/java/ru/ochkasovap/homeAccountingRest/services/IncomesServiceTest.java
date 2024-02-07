package ru.ochkasovap.homeAccountingRest.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;
import ru.ochkasovap.homeAccountingRest.models.CashAccount;
import ru.ochkasovap.homeAccountingRest.models.Income;
import ru.ochkasovap.homeAccountingRest.models.IncomeCategory;
import ru.ochkasovap.homeAccountingRest.models.Operation;
import ru.ochkasovap.homeAccountingRest.models.User;
import ru.ochkasovap.homeAccountingRest.util.DateRange;
import ru.ochkasovap.homeAccountingRest.util.OperationFilter;
import ru.ochkasovap.homeAccountingRest.util.OperationType;
import ru.ochkasovap.homeAccountingRest.util.exceptions.ForbiddenUsersActionException;

@ExtendWith(MockitoExtension.class)
class IncomesServiceTest {
	@InjectMocks
	private OperationsService service;
	@Mock
	private EntityManager entityManager;
	@Mock
	private UserService userService;
	@Mock
	private CashAccountsService accountsService;
	@Mock
	private CategoriesService categoriesService;
	
	private Income createdIncome;
	private Income editIncome;
	private List<Income> incomes;
	private User user;
	private List<CashAccount> accounts;
	private List<IncomeCategory> categories; 
	private OperationFilter filter;
	
	@BeforeEach
	void setUp() {
		accounts = new ArrayList<>();
		categories = new ArrayList<>();
		incomes = new ArrayList<>();
		initUser();
		initAccounts();
		initCategories();
		initIncomes();
		initFilter();
	}
	void initAccounts() {
		accounts.addAll(List.of(
				new CashAccount.Builder()
						.id(0)
						.name("CashAccount0")
						.balance(new BigDecimal(1000))
						.build(),
				new CashAccount.Builder()
						.id(1)
						.name("CashAccount1")
						.balance(new BigDecimal(1000))
						.build()
				));
	}
	void initCategories() {
		categories.addAll(List.of(
				(IncomeCategory)IncomeCategory.builder().id(0).name("Category0").build(),
				(IncomeCategory)IncomeCategory.builder().id(1).name("Category1").build()
				));
	}
	void initUser() {
		user = new User.Builder()
				.id(0)
				.cashAccounts(accounts)
				.incomeCategories(categories)
				.incomes(incomes)
				.build();
	}
	void initIncomes() {
		incomes.addAll(List.of(
				(Income)Income.builder().id(0).date(new Date()).amount(new BigDecimal(200))
				.cashAccount(accounts.get(0)).category(categories.get(0)).user(user).build(),
				(Income)Income.builder().id(1).date(new GregorianCalendar(2022,05,05).getTime()).amount(new BigDecimal(200))
				.cashAccount(accounts.get(1)).category(categories.get(0)).user(user).build(),
				(Income)Income.builder().id(2).date(new GregorianCalendar(2023,12,05).getTime()).amount(new BigDecimal(200))
				.cashAccount(accounts.get(0)).category(categories.get(1)).user(user).build(),
				(Income)Income.builder().id(3).date(new GregorianCalendar(2023,12,05).getTime()).amount(new BigDecimal(200))
				.cashAccount(accounts.get(0)).category(categories.get(1)).user(user).build()
				));
		createdIncome = (Income)Income.builder()
				.date(new Date())
				.amount(new BigDecimal(200))
				.cashAccount(accounts.get(0))
				.category(categories.get(0)).build();
		editIncome = (Income)Income.builder()
				.id(0)
				.date(new GregorianCalendar(2023,12,05).getTime())
				.amount(new BigDecimal(400))
				.cashAccount(accounts.get(1))
				.category(categories.get(1))
				.user(user).build();
	}
	void initFilter() {
		filter = new OperationFilter();
		filter.setType(OperationType.INCOME);
	}
	
	@Test
	void create() {
		when(userService.findById(0)).thenReturn(user);
		when(accountsService.findByNameAndUser("CashAccount0", user)).thenReturn(Optional.of(accounts.get(0)));
		when(categoriesService.findInDB(categories.get(0))).thenReturn(Optional.of(categories.get(0)));
		service.create(createdIncome, 0);
		Income actual = incomes.get(incomes.size()-1);
		assertEquals(new BigDecimal(1200), accounts.get(0).getBalance());
		assertEquals(createdIncome.getAmount(), actual.getAmount());
		assertEquals(createdIncome.getCashAccount(), actual.getCashAccount());
		assertEquals(createdIncome.getCategory(), actual.getCategory());
		assertEquals(createdIncome.getDate(), actual.getDate());
	}
	@Test
	void delete() {
		Income removedIncome = incomes.get(0);
		when(entityManager.find(Income.class, 0)).thenReturn(incomes.get(0));
		service.delete(0, 0, Income.class);
		assertFalse(incomes.contains(removedIncome));
		assertNull(removedIncome.getUser());
		assertEquals(new BigDecimal(800), accounts.get(0).getBalance());
	}
	@Test
	void delete_User_Without_Rights() {
		when(entityManager.find(Income.class, 0)).thenReturn(incomes.get(0));
		assertThrows(ForbiddenUsersActionException.class, () -> service.delete(1, 0, Income.class));
	}
	@Test
	void edit() {
		when(entityManager.find(Income.class, 0)).thenReturn(incomes.get(0));
		when(accountsService.findByNameAndUser("CashAccount1", user)).thenReturn(Optional.of(accounts.get(1)));
		when(categoriesService.findInDB(categories.get(1))).thenReturn(Optional.of(categories.get(1)));
		service.edit(editIncome);
		assertEquals(new BigDecimal(800), accounts.get(0).getBalance());
		assertEquals(new BigDecimal(1400), accounts.get(1).getBalance());
		assertEquals(categories.get(1), incomes.get(0).getCategory());
		assertEquals(accounts.get(1), incomes.get(0).getCashAccount());
		assertEquals(editIncome.getDate(), incomes.get(0).getDate());
	}
	@Test
	void edit_User_Without_Rights() {
		editIncome.setUser(new User.Builder().id(1).build());
		when(entityManager.find(Income.class, 0)).thenReturn(incomes.get(0));
		assertThrows(ForbiddenUsersActionException.class, () -> service.edit(editIncome));
	}
	@Test
	void findById() {
		Income findIncome = incomes.get(0);
		when(entityManager.find(Income.class, 0)).thenReturn(findIncome);
		assertEquals(findIncome, service.findById(0, 0, Income.class));
	}
	@Test
	void findById_User_Without_Rights() {
		Income findIncome = incomes.get(0);
		when(entityManager.find(Income.class, 0)).thenReturn(findIncome);
		assertThrows(ForbiddenUsersActionException.class, () -> service.findById(1, 0, Income.class));
	}
	@Test
	void findAll_WithoutFilter() {
		when(userService.findById(0)).thenReturn(user);
		List<? extends Operation> actual = service.findAll(filter, user.getId());
		List<? extends Operation> expected = List.of(incomes.get(1),incomes.get(2),incomes.get(3),incomes.get(0));
		assertEquals(expected, actual);
	}
	@Test
	void findAll_CategoryAndAccountFilter() {
		filter.setAccount(accounts.get(0).getName());
		filter.setCategory(categories.get(0).getName());
		when(userService.findById(0)).thenReturn(user);
		List<? extends Operation> actual = service.findAll(filter, user.getId());
		List<? extends Operation> expected = List.of(incomes.get(0));
		assertEquals(expected, actual);
	}
	@Test
	void findAll_DateRangeFilter() {
		when(userService.findById(0)).thenReturn(user);
		DateRange dateRange = new DateRange(new GregorianCalendar(2023,12,04).getTime(), new Date());
		filter.setDateRange(dateRange);
		List<? extends Operation> actual = service.findAll(filter, user.getId());
		List<? extends Operation> expected = List.of(incomes.get(2),incomes.get(3),incomes.get(0));
		assertEquals(expected, actual);
	}
	

}
