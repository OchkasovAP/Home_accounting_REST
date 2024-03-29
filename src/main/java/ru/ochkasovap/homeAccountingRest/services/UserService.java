package ru.ochkasovap.homeAccountingRest.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.ochkasovap.homeAccountingRest.models.CashAccount;
import ru.ochkasovap.homeAccountingRest.models.User;
import ru.ochkasovap.homeAccountingRest.repository.RoleRepository;
import ru.ochkasovap.homeAccountingRest.repository.UserRepository;


@Service
@Transactional(readOnly = true)
public class UserService {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder encoder;
	private final CategoriesService categoriesService;
	private final CashAccountsService accountsService;

	@Autowired
	public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder, CategoriesService categoriesService, CashAccountsService accountsService) {
		super();
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.encoder = encoder;
		this.categoriesService = categoriesService;
		this.accountsService = accountsService;
	}

	@Transactional
	public void create(User user) {
		user.setRole(roleRepository.findByName("USER").get());
		user.setPassword(encoder.encode(user.getPassword()));
		userRepository.save(user);
		categoriesService.addDefaultCategories(user.getId());
		user.setCashAccounts(new ArrayList<>());
		accountsService.create(user.getId(), new CashAccount.Builder()
				.balance(new BigDecimal(0))
				.containInGenBalance(true)
				.name("Основной").build());
	}

	@Transactional
	public void edit(User user) {
		User userFromDB = userRepository.findById(user.getId()).get();
		Hibernate.initialize(userFromDB);
		userFromDB.setLogin(user.getLogin());
		if(user.getPassword()!=null) {
			userFromDB.setPassword(encoder.encode(user.getPassword()));
		}
		if (user.getRole() != null) {
			String roleName = user.getRole().getName().toUpperCase();
			userFromDB.setRole(roleRepository.findByName(roleName.equals("ADMIN") ? "ADMIN" : "USER").get());
		}
		userRepository.save(userFromDB);
	}

	@Transactional
	public void delete(int id) {
		userRepository.deleteById(id);
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<User> findAll() {
		return userRepository.findAll();
	}

	@Transactional(readOnly = true)
	public User findById(int id) {
		return userRepository.findById(id).get();
	}

	@Transactional(readOnly = true)
	public Optional<User> findByLogin(String login) {
		return userRepository.findByLogin(login);
	}

}
