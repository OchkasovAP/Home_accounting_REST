package ru.ochkasovap.homeAccountingRest.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ru.ochkasovap.homeAccountingRest.models.User;
import ru.ochkasovap.homeAccountingRest.repository.UserRepository;
import ru.ochkasovap.homeAccountingRest.security.UserDetailsImpl;



@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetailsImpl loadUserByUsername(String login) throws UsernameNotFoundException {
		Optional<User> user = userRepository.findByLogin(login);
		if(user.isEmpty()) {
			throw new UsernameNotFoundException("Пользователь не найден");
		}
		return new UserDetailsImpl(user.get());
	}
}
