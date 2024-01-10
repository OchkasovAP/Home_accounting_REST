package ru.ochkasovap.homeAccountingRest.services;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import ru.ochkasovap.homeAccountingRest.models.Role;
import ru.ochkasovap.homeAccountingRest.models.User;
import ru.ochkasovap.homeAccountingRest.repository.RoleRepository;
import ru.ochkasovap.homeAccountingRest.repository.UserRepository;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {
	private UserService service;
	@Mock
	private UserRepository userRepository;
	@Mock
	private RoleRepository roleRepository;
	private PasswordEncoder encoder;

	private List<User> users;
	private User user;
	private Map<String, Role> roles;
	
	@BeforeEach
	void setUpEach() {
		encoder = new BCryptPasswordEncoder();
		service = new UserService(userRepository, roleRepository, encoder);
		roles = new HashMap<>(Map.of("ADMIN", new Role(0, "ADMIN"), "USER", new Role(1, "USER")));
		users = new ArrayList<>(List.of(
			new User.Builder()
					.id(0)
					.login("User0")
					.password("password0")
					.role(roles.get("ADMIN"))
					.build(),
			new User.Builder()
					.id(1)
					.login("User1")
					.password("password1")
					.role(roles.get("USER"))
					.build()
		));
		user = new User.Builder()
				.id(2)
				.login("User2")
				.password("password2")
				.role(roles.get("USER"))
				.build();
	}
	@Test
	void create() {
		when(userRepository.save(user)).thenAnswer(a -> {
			users.add(user);
			return user;
		});
		String userPassword = user.getPassword();
		when(roleRepository.findByName("USER")).thenReturn(Optional.of(roles.get("USER")));
		service.create(user);
		assertEquals(user.getRole(), roles.get("USER"));
		assertTrue(encoder.matches(userPassword, user.getPassword()));
		assertTrue(users.contains(user));
	}
	
	@Test
	void edit_CurrentLoginAndUpdatePassword() {
		User userFromDB = users.get(1);
		User user = new User.Builder()
				.login(userFromDB.getLogin())
				.id(userFromDB.getId())
				.password("NewPassword")
				.build();
		String userPassword = user.getPassword();
		when(userRepository.findById(1)).thenReturn(Optional.of(userFromDB));
		when(userRepository.save(userFromDB)).thenReturn(userFromDB);
		service.edit(user);
		assertTrue(encoder.matches(userPassword, userFromDB.getPassword()));
	}
	@Test
	void edit_ChangeRole() {
		User userFromDB = users.get(1);
		User user = new User.Builder()
				.login(userFromDB.getLogin())
				.id(userFromDB.getId())
				.password(userFromDB.getPassword())
				.role(roles.get("ADMIN"))
				.build();
		when(userRepository.findById(1)).thenReturn(Optional.of(userFromDB));
		when(userRepository.save(userFromDB)).thenReturn(userFromDB);
		when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(roles.get("ADMIN")));
		service.edit(user);
		assertEquals(roles.get("ADMIN"), userFromDB.getRole());
	}
	@Test
	void edit_UpdateLoginAndCurrentPassword() {
		User userFromDB = users.get(1);
		User user = new User.Builder()
				.login("NewUserLogin")
				.id(userFromDB.getId())
				.password(userFromDB.getPassword())
				.build();
		when(userRepository.findById(1)).thenReturn(Optional.of(users.get(1)));
		when(userRepository.save(userFromDB)).thenReturn(userFromDB);
		service.edit(user);
		assertEquals(userFromDB.getLogin(), user.getLogin());
	}

}
