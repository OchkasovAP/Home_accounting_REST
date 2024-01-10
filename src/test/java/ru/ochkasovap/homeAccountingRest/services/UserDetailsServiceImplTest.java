package ru.ochkasovap.homeAccountingRest.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import ru.ochkasovap.homeAccountingRest.models.User;
import ru.ochkasovap.homeAccountingRest.repository.UserRepository;
import ru.ochkasovap.homeAccountingRest.security.UserDetailsImpl;
@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {
	@InjectMocks
	private UserDetailsServiceImpl service;
	@Mock
	private UserRepository userRepository;
	private User user;
	@BeforeEach
	void setUp() {
		user = new User.Builder().id(0).login("Login").build();
	}
	@Test
	void loadUserByUserName() {
		when(userRepository.findByLogin("Login")).thenReturn(Optional.of(user));
		UserDetailsImpl expected = new UserDetailsImpl(user);
		UserDetailsImpl actual = service.loadUserByUsername("Login");
		assertEquals(expected.getUser(), actual.getUser());
	}
	@Test
	void loadUserByUserName_UserNotFound() {
		when(userRepository.findByLogin("Login")).thenReturn(Optional.empty());
		assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("Login"),"Пользователь не найден");
	}

}
