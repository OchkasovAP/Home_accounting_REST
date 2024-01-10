package ru.ochkasovap.homeAccountingRest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.ochkasovap.homeAccountingRest.models.User;


@Repository
public interface UserRepository extends JpaRepository<User, Integer>{
	Optional<User> findByLogin(String login);
}
