package ru.ochkasovap.homeAccountingRest.services;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.ochkasovap.homeAccountingRest.models.CashAccount;
import ru.ochkasovap.homeAccountingRest.models.User;
import ru.ochkasovap.homeAccountingRest.repository.CashAccountRepository;
import ru.ochkasovap.homeAccountingRest.repository.UserRepository;
import ru.ochkasovap.homeAccountingRest.util.exceptions.ForbiddenUsersActionException;



@Service
@Transactional(readOnly = true)
public class CashAccountsService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CashAccountRepository accountRepository;
	
	public BigDecimal getGeneralBalance(List<CashAccount> cashAccounts) {
		BigDecimal generalBalance = new BigDecimal(0);
		for (CashAccount cashAccount : cashAccounts) {
			if (cashAccount.getContainInGenBalance()) {
				generalBalance = generalBalance.add(cashAccount.getBalance());
			}
		}
		return generalBalance;
	}
	

	public List<CashAccount> findAllByUser(int userId) {
		Optional<User> user = userRepository.findById(userId);
		if(user.isEmpty()) {
			return Collections.emptyList();
		}
		return user.get().getCashAccounts().stream().sorted(Comparator.comparing(a -> a.getId())).toList();
	}

	public CashAccount findById(int userId, int id) {
		CashAccount account = accountRepository.findById(id).get();
		checkUsersRights(account, userId);
		return account;
	}
	
	public Optional<CashAccount> findByNameAndUser(String name, User user) {
		return accountRepository.findByNameAndUser(name, user);
	}
	
	@Transactional
	public void edit(CashAccount cashAccount) {
		User user = accountRepository.findById(cashAccount.getId()).get().getUser();
		checkUsersRights(cashAccount, user.getId());
		accountRepository.save(cashAccount);
	}
	@Transactional
	public void create(int userId, CashAccount cashAccount) {
		User user = userRepository.findById(userId).get();
		user.getCashAccounts().add(cashAccount);
		cashAccount.setUser(user);
	}
	@Transactional
	public void remove(int userId, int id) {
		findById(userId, id);
		accountRepository.deleteById(id);
	}
	private void checkUsersRights(CashAccount account, int userId) {
		if(account.getUser().getId()!=userId) {
			throw new ForbiddenUsersActionException();
		}
	}
	
} 
