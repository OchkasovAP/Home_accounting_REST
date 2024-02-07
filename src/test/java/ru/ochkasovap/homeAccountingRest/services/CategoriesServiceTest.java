package ru.ochkasovap.homeAccountingRest.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;
import ru.ochkasovap.homeAccountingRest.models.Category;
import ru.ochkasovap.homeAccountingRest.models.IncomeCategory;
import ru.ochkasovap.homeAccountingRest.models.OutcomeCategory;
import ru.ochkasovap.homeAccountingRest.models.User;
import ru.ochkasovap.homeAccountingRest.repository.UserRepository;
import ru.ochkasovap.homeAccountingRest.util.OperationType;
import ru.ochkasovap.homeAccountingRest.util.exceptions.ForbiddenUsersActionException;

@ExtendWith(MockitoExtension.class)
class CategoriesServiceTest {
	@InjectMocks
	private CategoriesService service;
	@Mock
	private UserRepository userRepository;
	@Mock
	private EntityManager entityManager;
	
	private List<IncomeCategory> incomeList;
	private List<OutcomeCategory> outcomeList;
	private User user;
	@BeforeEach
	void setUp() {
		outcomeList = new ArrayList<>();
		incomeList = new ArrayList<>();
		user = new User.Builder().id(0).incomeCategories(incomeList).outcomeCategories(outcomeList).build();
		incomeList.addAll(List.of(
				(IncomeCategory)IncomeCategory.builder().id(2).name("IncomeCategory2").user(user).build(),
				(IncomeCategory)IncomeCategory.builder().id(1).name("IncomeCategory1").user(user).build(),
				(IncomeCategory)IncomeCategory.builder().id(0).name("IncomeCategory0").user(user).build()
				));
		outcomeList.addAll(List.of(
				(OutcomeCategory)OutcomeCategory.builder().id(2).name("OutcomeCategory2").user(user).build(),
				(OutcomeCategory)OutcomeCategory.builder().id(1).name("OutcomeCategory1").user(user).build(),
				(OutcomeCategory)OutcomeCategory.builder().id(0).name("OutcomeCategory0").user(user).build()
				));
	}
	@Test
	void findAllByUser_OutcomeCategories() {
		when(userRepository.findById(0)).thenReturn(Optional.of(user));
		List<? extends Category> actual = service.findAllByUser(0, OperationType.OUTCOME);
		assertNotEquals(outcomeList, actual);
		assertEquals(outcomeList.stream().sorted(Comparator.comparing(c -> c.getId())).toList(), actual);
	}
	@Test
	void findAllByUser_IncomeCategories() {
		when(userRepository.findById(0)).thenReturn(Optional.of(user));
		List<? extends Category> actual = service.findAllByUser(0, OperationType.INCOME);
		assertNotEquals(incomeList, actual);
		assertEquals(incomeList.stream().sorted(Comparator.comparing(c -> c.getId())).toList(), actual);
	}
	@Test
	void findAllByUser_NullUser() {
		when(userRepository.findById(0)).thenThrow(NoSuchElementException.class);
		assertThrows(NoSuchElementException.class, () -> service.findAllByUser(0, OperationType.INCOME));
	}
	@Test 
	void findAllByUser_NonCorrectType() {
		when(userRepository.findById(0)).thenReturn(Optional.of(user));
		assertEquals(Collections.emptyList(), service.findAllByUser(0, null));
	}
	@Test
	void create_CorrectArgs_OutcomeCategory() {
		when(userRepository.findById(0)).thenReturn(Optional.of(user));
		Category outcome = OutcomeCategory.builder().id(3).name("New Outcome").build();
		service.create(0, outcome);
		assertTrue(user.getOutcomeCategories().contains(outcome));
		assertTrue(outcome.getUser().equals(user));
	}
	@Test
	void create_CorrectArgs_IncomeCategory() {
		when(userRepository.findById(0)).thenReturn(Optional.of(user));
		Category income = IncomeCategory.builder().id(3).name("NewIncome").build();
		service.create(0, income);
		assertTrue(user.getIncomeCategories().contains(income));
		assertTrue(income.getUser().equals(user));
	}
	@Test
	void create_NullUser_OutcomeCategory() {
		when(userRepository.findById(0)).thenThrow(NoSuchElementException.class);
		Category outcome = OutcomeCategory.builder().id(3).name("New Outcome").build();
		assertThrows(NoSuchElementException.class, () -> service.create(0, outcome));
	}
	@Test
	void create_NullUser_IncomeCategory() {
		when(userRepository.findById(0)).thenThrow(NoSuchElementException.class);
		Category income = IncomeCategory.builder().id(3).name("New Income").build();
		assertThrows(NoSuchElementException.class, () -> service.create(0, income));
	}

	@Test
	void remove_CorrectArgs_OutcomeCategory() {
		OutcomeCategory outcome = outcomeList.get(0);
		when(entityManager.find(OutcomeCategory.class, 0)).thenReturn(outcome);
		service.remove(0, 0, OperationType.OUTCOME);
		assertFalse(user.getOutcomeCategories().contains(outcome));
		assertNull(outcome.getUser());
	}
	@Test
	void remove_CorrectArgs_IncomeCategory() {
		IncomeCategory income = incomeList.get(0);
		when(entityManager.find(IncomeCategory.class, 0)).thenReturn(income);
		service.remove(0, 0, OperationType.INCOME);
		assertFalse(user.getIncomeCategories().contains(income));
		assertNull(income.getUser());
	}
	@Test
	void remove_User_WithoutRights() {
		IncomeCategory income = incomeList.get(0);
		when(entityManager.find(IncomeCategory.class, 0)).thenReturn(income);
		assertThrows(ForbiddenUsersActionException.class, () -> service.remove(1, 0, OperationType.INCOME));
	}
	@Test
	void edit_Correct() {
		Category category = IncomeCategory.builder().id(0).name("EditCategoryName").user(user).build();
		IncomeCategory categoryFromDB = incomeList.get(0);
		when(entityManager.find(IncomeCategory.class, 0)).thenReturn(categoryFromDB);
		service.edit(category);
		assertEquals(category.getName(), categoryFromDB.getName());
	}
	@Test
	void edit_User_WithoutRights() {
		Category category = IncomeCategory.builder().id(0).name("EditCategoryName").user(new User.Builder().id(3).build()).build();
		IncomeCategory categoryFromDB = incomeList.get(0);
		when(entityManager.find(IncomeCategory.class, 0)).thenReturn(categoryFromDB);
		assertThrows(ForbiddenUsersActionException.class, () -> service.edit(category));
	}
	@Test
	void findById() {
		when(entityManager.find(IncomeCategory.class, 0)).thenReturn(incomeList.get(0));
		assertEquals(incomeList.get(0), service.findById(0, 0, IncomeCategory.class));
	}
	@Test
	void findById_User_Without_Rights() {
		when(entityManager.find(IncomeCategory.class, 0)).thenReturn(incomeList.get(0));
		assertThrows(ForbiddenUsersActionException.class, () -> service.findById(1, 0, IncomeCategory.class));
	}
}
