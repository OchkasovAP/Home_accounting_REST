package ru.ochkasovap.homeAccountingRest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.ochkasovap.homeAccountingRest.models.CashAccount;
import ru.ochkasovap.homeAccountingRest.models.User;



@Repository
public interface CashAccountRepository extends JpaRepository<CashAccount, Integer>{
	Optional<CashAccount> findByNameAndUser(String name, User user);
}
