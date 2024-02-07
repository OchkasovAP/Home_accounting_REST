package ru.ochkasovap.homeAccountingRest.controllers;
 
import java.util.List;
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
import ru.ochkasovap.homeAccountingRest.dto.OperationDTO;
import ru.ochkasovap.homeAccountingRest.dto.OperationFilterDTO;
import ru.ochkasovap.homeAccountingRest.models.CashAccount;
import ru.ochkasovap.homeAccountingRest.models.Operation;
import ru.ochkasovap.homeAccountingRest.models.User;
import ru.ochkasovap.homeAccountingRest.security.UserDetailsImpl;
import ru.ochkasovap.homeAccountingRest.services.OperationsService;
import ru.ochkasovap.homeAccountingRest.util.OperationFilter;
import ru.ochkasovap.homeAccountingRest.util.OperationType;
import ru.ochkasovap.homeAccountingRest.util.exceptions.HomeAccountingException;
import ru.ochkasovap.homeAccountingRest.util.exceptions.OperationNotValidException;
import ru.ochkasovap.homeAccountingRest.util.validators.OperationValidator;

@RestController
@RequestMapping("/operations")
public class OperationsController extends AbstractHomeAccountingController{
	
	private final OperationsService operationsService;
	private final OperationValidator validator;
	private final ModelMapper modelMapper;
	
	@Autowired
	public OperationsController(OperationsService operationsService, OperationValidator validator, ModelMapper modelMapper) {
		super();
		this.operationsService = operationsService;
		this.validator = validator;
		this.modelMapper = modelMapper;
	}

	@GetMapping("/{type}")
	public List<OperationDTO> showOperationsList(@PathVariable("type") OperationType type, @RequestBody OperationFilterDTO filter,
			@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return operationsService.findAll(convertFilterDTO(filter, type), userDetails.getUser().getId())
				.stream()
				.map(this::convertOperation)
				.toList();
	}
	
	@GetMapping("/{type}/{id}")
	public OperationDTO showOperation(@PathVariable("type") OperationType type, @PathVariable("id") int operationID, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		try {
			Operation operation = operationsService.findById(userDetails.getUser().getId(), operationID, type.getOperationClass());
			return convertOperation(operation);
		} catch (NoSuchElementException ex) {
			throw new HomeAccountingException("Операции с таким id не существует в базе данных");
		}
	}
	
	@PostMapping("/{type}")
	public ResponseEntity<Void> createOperation(@PathVariable("type") OperationType type, @RequestBody @Valid OperationDTO operationDTO,
			BindingResult bindingResult, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		Operation operation = convertDTO(operationDTO, type);
		operation.setUser(userDetails.getUser());
		validator.validate(operation, bindingResult);
		if(bindingResult.hasErrors()) {
			throw new OperationNotValidException(bindingResult);
		}
		operationsService.create(operation, userDetails.getUser().getId());
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PatchMapping("/{type}")
	public ResponseEntity<Void> editOperation(@RequestBody @Valid OperationDTO operationDTO, BindingResult bindingResult,
			 @PathVariable("type") OperationType type, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		Operation operation = convertDTO(operationDTO, type);
		User user = userDetails.getUser();
		operationsService.checkUsersRights(operation, user.getId());
		operation.setUser(user);
		validator.validate(operation, bindingResult);
		if(bindingResult.hasErrors()) {
			throw new OperationNotValidException(bindingResult);
		}
		operation.setUser(userDetails.getUser());
		operationsService.edit(operation);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/{type}/{id}")
	public ResponseEntity<Void> deleteOperation(@PathVariable("id") int operationID,
			@PathVariable("type") OperationType type, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		int userId = userDetails.getUser().getId();
		Class<Operation> operationClass = type.getOperationClass();
		operationsService.delete(userId, operationID, operationClass);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	private OperationDTO convertOperation(Operation operation) {
		OperationDTO operationDTO = modelMapper.map(operation, OperationDTO.class);
		operationDTO.setAccount(operation.getCashAccount().getName());
		operationDTO.setCategory(operation.getCategory().getName());
		return operationDTO;
	}
	private Operation convertDTO(OperationDTO operationDTO, OperationType type) {
		Class<Operation> operClass = type.getOperationClass();
		Operation operation = modelMapper.map(operationDTO, operClass);
		operation.setCashAccount(new CashAccount.Builder().name(operationDTO.getAccount()).build());
		operation.setCategory(type.newCategory(operationDTO.getCategory()));
		return operation;
	}
	private OperationFilter convertFilterDTO(OperationFilterDTO filterDTO, OperationType type) {
		OperationFilter filter = modelMapper.map(filterDTO, OperationFilter.class);
		filter.setType(type);
		return filter;
	}
	
}
