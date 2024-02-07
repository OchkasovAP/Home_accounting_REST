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
import ru.ochkasovap.homeAccountingRest.models.Operation;
import ru.ochkasovap.homeAccountingRest.models.Outcome;
import ru.ochkasovap.homeAccountingRest.models.OutcomeCategory;
import ru.ochkasovap.homeAccountingRest.models.User;
import ru.ochkasovap.homeAccountingRest.util.DateRange;
import ru.ochkasovap.homeAccountingRest.util.OperationFilter;
import ru.ochkasovap.homeAccountingRest.util.OperationType;
import ru.ochkasovap.homeAccountingRest.util.exceptions.ForbiddenUsersActionException;


@ExtendWith(MockitoExtension.class)
class OutcomesServiceTest {
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
	
	private Outcome createdOutcome;
	private Outcome editOutcome;
	private List<Outcome> outcomes;
	private User user;
	private List<CashAccount> accounts;
	private List<OutcomeCategory> categories; 
	private OperationFilter filter;
	
	@BeforeEach
	void setUp() {
		accounts = new ArrayList<>();
		categories = new ArrayList<>();
		outcomes = new ArrayList<>();
		initUser();
		initAccounts();
		initCategories();
		initOutcomes();
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
				(OutcomeCategory)OutcomeCategory.builder().id(0).name("Category0").build(),
				(OutcomeCategory)OutcomeCategory.builder().id(1).name("Category1").build()
				));
	}
	void initUser() {
		user = new User.Builder()
				.id(0)
				.cashAccounts(accounts)
				.outcomeCategories(categories)
				.outcomes(outcomes)
				.build();
	}
	void initOutcomes() {
		outcomes.addAll(List.of(
				(Outcome)Outcome.builder().id(0).date(new Date()).amount(new BigDecimal(200))
				.cashAccount(accounts.get(0)).category(categories.get(0)).user(user).build(),
				(Outcome)Outcome.builder().id(1).date(new GregorianCalendar(2022,05,05).getTime()).amount(new BigDecimal(200))
				.cashAccount(accounts.get(1)).category(categories.get(0)).user(user).build(),
				(Outcome)Outcome.builder().id(2).date(new GregorianCalendar(2023,12,05).getTime()).amount(new BigDecimal(200))
				.cashAccount(accounts.get(0)).category(categories.get(1)).user(user).build(),
				(Outcome)Outcome.builder().id(3).date(new GregorianCalendar(2023,12,05).getTime()).amount(new BigDecimal(200))
				.cashAccount(accounts.get(0)).category(categories.get(1)).user(user).build()
				));
		createdOutcome = (Outcome)Outcome.builder()
				.date(new Date())
				.amount(new BigDecimal(200))
				.cashAccount(accounts.get(0))
				.category(categories.get(0)).build();
		editOutcome = (Outcome)Outcome.builder()
				.id(0)
				.date(new GregorianCalendar(2023,12,05).getTime())
				.amount(new BigDecimal(400))
				.cashAccount(accounts.get(1))
				.category(categories.get(1))
				.user(user).build();
	}
	void initFilter() {
		filter = new OperationFilter();
		filter.setType(OperationType.OUTCOME);
	}
	
	@Test
	void create() {
		when(userService.findById(0)).thenReturn(user);
		when(accountsService.findByNameAndUser("CashAccount0", user)).thenReturn(Optional.of(accounts.get(0)));
		when(categoriesService.findInDB(categories.get(0))).thenReturn(Optional.of(categories.get(0)));
		service.create(createdOutcome, 0);
		Outcome actual = outcomes.get(outcomes.size()-1);
		assertEquals(new BigDecimal(800), accounts.get(0).getBalance());
		assertEquals(createdOutcome.getAmount(), actual.getAmount());
		assertEquals(createdOutcome.getCashAccount(), actual.getCashAccount());
		assertEquals(createdOutcome.getCategory(), actual.getCategory());
		assertEquals(createdOutcome.getDate(), actual.getDate());
	}
	@Test
	void delete() {
		Outcome removedIncome = outcomes.get(0);
		when(entityManager.find(Outcome.class, 0)).thenReturn(outcomes.get(0));
		service.delete(0, 0, Outcome.class);
		assertFalse(outcomes.contains(removedIncome));
		assertNull(removedIncome.getUser());
		assertEquals(new BigDecimal(1200), accounts.get(0).getBalance());
	}
	@Test
	void delete_User_Without_Rights() {
		when(entityManager.find(Outcome.class, 0)).thenReturn(outcomes.get(0));
		assertThrows(ForbiddenUsersActionException.class, () -> service.delete(1, 0, Outcome.class));
	}
	@Test
	void edit() {
		when(entityManager.find(Outcome.class, 0)).thenReturn(outcomes.get(0));
		when(accountsService.findByNameAndUser("CashAccount1", user)).thenReturn(Optional.of(accounts.get(1)));
		when(categoriesService.findInDB(categories.get(1))).thenReturn(Optional.of(categories.get(1)));
		service.edit(editOutcome);
		assertEquals(new BigDecimal(1200), accounts.get(0).getBalance());
		assertEquals(new BigDecimal(600), accounts.get(1).getBalance());
		assertEquals(categories.get(1), outcomes.get(0).getCategory());
		assertEquals(accounts.get(1), outcomes.get(0).getCashAccount());
		assertEquals(editOutcome.getDate(), outcomes.get(0).getDate());
	}
	@Test
	void edit_User_Without_Rights() {
		editOutcome.setUser(new User.Builder().id(1).build());
		when(entityManager.find(Outcome.class, 0)).thenReturn(outcomes.get(0));
		assertThrows(ForbiddenUsersActionException.class, () -> service.edit(editOutcome));
	}
	@Test
	void findById() {
		Outcome findIncome = outcomes.get(0);
		when(entityManager.find(Outcome.class, 0)).thenReturn(findIncome);
		assertEquals(findIncome, service.findById(0, 0, Outcome.class));
	}
	@Test
	void findById_User_Without_Rights() {
		Outcome findIncome = outcomes.get(0);
		when(entityManager.find(Outcome.class, 0)).thenReturn(findIncome);
		assertThrows(ForbiddenUsersActionException.class, () -> service.findById(1, 0, Outcome.class));
	}
	@Test
	void findAll_WithoutFilter() {
		when(userService.findById(0)).thenReturn(user);
		List<? extends Operation> actual = service.findAll(filter, user.getId());
		List<? extends Operation> expected = List.of(outcomes.get(1),outcomes.get(2),outcomes.get(3),outcomes.get(0));
		assertEquals(expected, actual);
	}
	@Test
	void findAll_CategoryAndAccountFilter() {
		filter.setAccount(accounts.get(0).getName());
		filter.setCategory(categories.get(0).getName());
		when(userService.findById(0)).thenReturn(user);
		List<? extends Operation> actual = service.findAll(filter, user.getId());
		List<? extends Operation> expected = List.of(outcomes.get(0));
		assertEquals(expected, actual);
	}
	@Test
	void findAll_DateRangeFilter() {
		when(userService.findById(0)).thenReturn(user);
		DateRange dateRange = new DateRange(new GregorianCalendar(2023,12,04).getTime(), new Date());
		filter.setDateRange(dateRange);
		List<? extends Operation> actual = service.findAll(filter, user.getId());
		List<? extends Operation> expected = List.of(outcomes.get(2),outcomes.get(3),outcomes.get(0));
		assertEquals(expected, actual);
	}
}
