package ru.ochkasovap.homeAccountingRest.controllers;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import ru.ochkasovap.homeAccountingRest.dto.AccountDTO;
import ru.ochkasovap.homeAccountingRest.models.CashAccount;
import ru.ochkasovap.homeAccountingRest.security.UserDetailsImpl;
import ru.ochkasovap.homeAccountingRest.services.CashAccountsService;
import ru.ochkasovap.homeAccountingRest.util.exceptions.AccountNotValidException;
import ru.ochkasovap.homeAccountingRest.util.exceptions.HomeAccountingException;
import ru.ochkasovap.homeAccountingRest.util.validators.AccountValidator;


@RestController
@RequestMapping("/cashAccounts")
public class CashAccountController extends AbstractHomeAccountingController{

	private final CashAccountsService accountService;
	private final ModelMapper modelMapper;
	private final AccountValidator validator;
	
	@Autowired
	public CashAccountController(CashAccountsService accountService, ModelMapper modelMapper,
			AccountValidator validator) {
		super();
		this.accountService = accountService;
		this.modelMapper = modelMapper;
		this.validator = validator;
	}
	
	@GetMapping()
	public ResponseEntity<Map<String, Object>> showCashAccounts(@AuthenticationPrincipal UserDetailsImpl userDetails) {
		int userID = userDetails.getUser().getId();
		List<CashAccount> cashAccounts = accountService.findAllByUser(userID);
		List<AccountDTO> accountDTOList = cashAccounts.stream().map(this::convertAccount).toList();
		BigDecimal generalBalance = accountService.getGeneralBalance(cashAccounts);
		return new ResponseEntity<Map<String,Object>>(Map.of("accounts", accountDTOList, "generalBalance", generalBalance), HttpStatus.OK);
	}
	@GetMapping("/{id}")
	public ResponseEntity<AccountDTO> cashAccountInfo(@PathVariable("id") int cashAccID, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		try {
			return new ResponseEntity<>(convertAccount(accountService.findById(userDetails.getUser().getId(), cashAccID)), HttpStatus.OK);
		} catch (NoSuchElementException ex) {
			throw new HomeAccountingException("Счет с таким id не найден в базе данных");
		}
	}
	
	@PostMapping()
	public ResponseEntity<Void> addNewCashAccount(@AuthenticationPrincipal UserDetailsImpl userDetails,
			@RequestBody @Valid AccountDTO accountDTO, BindingResult bindingResult) {
		CashAccount cashAccount = convertDTO(accountDTO);
		cashAccount.setUser(userDetails.getUser());
		validator.validate(cashAccount, bindingResult);
		if(bindingResult.hasErrors()) {
			throw new AccountNotValidException(bindingResult);
		}
		accountService.create(userDetails.getUser().getId(), cashAccount);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> removeCashAccount(@PathVariable("id") int cashAccountID, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		accountService.remove(userDetails.getUser().getId(), cashAccountID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PatchMapping()
	public ResponseEntity<Void> editCashAccount(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody @Valid AccountDTO accountDTO, BindingResult bindingResult) {
		CashAccount cashAccount = convertDTO(accountDTO);
		validator.validate(cashAccount, bindingResult);
		if(bindingResult.hasErrors()) {
			throw new AccountNotValidException(bindingResult);
		}
		cashAccount.setUser(userDetails.getUser());
		accountService.edit(cashAccount);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	private AccountDTO convertAccount(CashAccount account) {
		return modelMapper.map(account, AccountDTO.class);
	}
	private CashAccount convertDTO(AccountDTO account) {
		return modelMapper.map(account, CashAccount.class);
	}
}
