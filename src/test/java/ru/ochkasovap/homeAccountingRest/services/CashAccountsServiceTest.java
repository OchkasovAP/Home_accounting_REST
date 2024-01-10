package ru.ochkasovap.homeAccountingRest.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
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

import ru.ochkasovap.homeAccountingRest.models.CashAccount;
import ru.ochkasovap.homeAccountingRest.models.User;
import ru.ochkasovap.homeAccountingRest.repository.CashAccountRepository;
import ru.ochkasovap.homeAccountingRest.repository.UserRepository;
import ru.ochkasovap.homeAccountingRest.util.exceptions.ForbiddenUsersActionException;


@ExtendWith(MockitoExtension.class)
class CashAccountsServiceTest {
	@InjectMocks
	private CashAccountsService service;
	@Mock
	private UserRepository userRepository;
	@Mock
	private CashAccountRepository accountRepository;
	
	private List<CashAccount> accounts;
	private CashAccount account;
	private CashAccount editAccount;
	private User user;
	@BeforeEach
	void setUp() {
		user = new User.Builder().id(0).build();
		accounts = new ArrayList<>();
		accounts.add(new CashAccount.Builder()
				.id(2)
				.name("CashAccount2")
				.balance(new BigDecimal(100))
				.containInGenBalance(true)
				.user(user)
				.build());
		accounts.add(new CashAccount.Builder()
				.id(1)
				.name("CashAccount1")
				.balance(new BigDecimal(100))
				.containInGenBalance(true)
				.user(user)
				.build());
		accounts.add(new CashAccount.Builder()
				.id(0)
				.name("CashAccount0")
				.balance(new BigDecimal(100))
				.containInGenBalance(false)
				.user(user)
				.build());
		account = new CashAccount.Builder()
				.id(3)
				.name("CashAccount3")
				.balance(new BigDecimal(100))
				.containInGenBalance(true)
				.build();
		editAccount = new CashAccount.Builder()
				.id(0)
				.name("EditCashAccount")
				.balance(new BigDecimal(200))
				.containInGenBalance(true)
				.user(user)
				.build();
	}
	@Test
	void getGeneralBalance() {
		BigDecimal expected = new BigDecimal(200);
		BigDecimal actual = service.getGeneralBalance(accounts);
		assertEquals(expected, actual);
	}
	
	@Test
	void findAllByUser_PresentUser() {
		when(userRepository.findById(0)).thenReturn(Optional.of(new User.Builder().cashAccounts(accounts).build()));
		List<CashAccount> actual = service.findAllByUser(0);
		assertNotEquals(accounts, actual);
		assertEquals(accounts.stream().sorted(Comparator.comparing(ca -> ca.getId())).toList(), actual);
	}
	
	@Test
	void findAllByUser_NullUser() {
		when(userRepository.findById(0)).thenReturn(Optional.empty());
		assertEquals(Collections.emptyList(), service.findAllByUser(0));
	}
	@Test
	void create_CorrectArguments() {
		User user = new User.Builder().cashAccounts(accounts).build();
		CashAccount cashAccount = account;
		when(userRepository.findById(0)).thenReturn(Optional.of(user));
		service.create(0, cashAccount);
		assertEquals(user, cashAccount.getUser());
		assertTrue(user.getCashAccounts().contains(cashAccount));
	}
	
	@Test
	void create_UserDoesntExist() {
		when(userRepository.findById(0)).thenReturn(Optional.empty());
		assertThrows(NoSuchElementException.class, () -> service.create(0, null));
	}

	@Test
	void create_NullCashAccount() {
		when(userRepository.findById(0)).thenReturn(Optional.of(new User.Builder().cashAccounts(accounts).build()));
		assertThrows(NullPointerException.class, () -> service.create(0, null));
	}
	@Test
	void findById() {
		when(accountRepository.findById(0)).thenReturn(Optional.of(accounts.get(0)));
		assertEquals(accounts.get(0),service.findById(0, 0));
	} 
	@Test
	void findById_User_Without_Rights() {
		when(accountRepository.findById(0)).thenReturn(Optional.of(accounts.get(0)));
		assertThrows(ForbiddenUsersActionException.class, () -> service.findById(1, 0));
	} 
	
	@Test 
	void findByNameAndUser() {
		when(accountRepository.findByNameAndUser("CashAccount3", user)).thenReturn(Optional.of(account));
		assertEquals(account, service.findByNameAndUser(account.getName(), user).get());
	}
	
	@Test
	void edit() {
		when(accountRepository.findById(0)).thenReturn(Optional.of(editAccount));
		when(accountRepository.save(editAccount)).thenReturn(editAccount);
		assertDoesNotThrow(() -> service.edit(editAccount));
	}
	@Test
	void edit_User_Without_Rights() {
		when(accountRepository.findById(0)).thenReturn(Optional.of(new CashAccount.Builder().user(new User.Builder().id(1).build()).build()));
		assertThrows(ForbiddenUsersActionException.class,() -> service.edit(editAccount));
	}
	@Test
	void remove() {
		doNothing().when(accountRepository).deleteById(0);
		when(accountRepository.findById(0)).thenReturn(Optional.of(accounts.get(0)));
		assertDoesNotThrow(() -> service.remove(0,0));
	}
	@Test
	void remove_User_Without_Rights() {
		when(accountRepository.findById(0)).thenReturn(Optional.of(accounts.get(0)));
		assertThrows(ForbiddenUsersActionException.class,() -> service.remove(1,0));
	}
 
}
