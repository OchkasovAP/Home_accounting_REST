package ru.ochkasovap.homeAccountingRest.services;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import ru.ochkasovap.homeAccountingRest.dto.DateRange;
import ru.ochkasovap.homeAccountingRest.dto.OperationDTO;
import ru.ochkasovap.homeAccountingRest.models.CashAccount;
import ru.ochkasovap.homeAccountingRest.models.Income;
import ru.ochkasovap.homeAccountingRest.models.Outcome;
import ru.ochkasovap.homeAccountingRest.models.User;
import ru.ochkasovap.homeAccountingRest.repository.CashAccountRepository;
import ru.ochkasovap.homeAccountingRest.repository.UserRepository;
import ru.ochkasovap.homeAccountingRest.util.Category;
import ru.ochkasovap.homeAccountingRest.util.DateUtil;
import ru.ochkasovap.homeAccountingRest.util.Operation;
import ru.ochkasovap.homeAccountingRest.util.OperationType;
import ru.ochkasovap.homeAccountingRest.util.exceptions.ForbiddenUsersActionException;


@Service
@Transactional(readOnly = true)
public class OperationsService {
	private final EntityManager entityManager;
	private final UserService userService;
	private final CashAccountsService accountsService;
	private final CategoriesService categoriesService;
	
	@Autowired
	public OperationsService(EntityManager entityManager, UserService userService, CashAccountsService accountsService,
			CategoriesService categoriesService) {
		super();
		this.entityManager = entityManager;
		this.userService = userService;
		this.accountsService = accountsService;
		this.categoriesService = categoriesService;
	}

	@Transactional
	public void create(Operation model, int userId) {
		User user = userService.findById(userId);
		model.setUser(user);
		Operation operation = model.getType().newEmptyOperation();
		foundAccountAndCategory(model);
		fillOperationFromModel(operation,model);
		if(OperationType.INCOME.equals(operation.getType())) {
			addToAccountBalance(operation);
			user.getIncomes().add((Income)operation);
		} else if(OperationType.OUTCOME.equals(operation.getType())) {
			subtractFromAccountBalance(operation);
			user.getOutcomes().add((Outcome)operation);
		}
		operation.setUser(user);
	}
	
	@Transactional
	public void delete(int userId, int operationId, Class<? extends Operation> operClass) {
		Operation operation = entityManager.find(operClass, operationId);
		User user = operation.getUser();
		checkUsersRights(operation, userId);
		if(OperationType.INCOME.equals(operation.getType())) {
			subtractFromAccountBalance(operation);
			user.getIncomes().remove(operation);
		} else if(OperationType.OUTCOME.equals(operation.getType())) {
			addToAccountBalance(operation);
			user.getOutcomes().remove(operation);
		}
		operation.setUser(null);
	}

	@Transactional
	public void edit(Operation model) {
		OperationType operationType = model.getType();
		Operation editOperation = entityManager.find(operationType.getOperationClass(), model.getId());
		checkUsersRights(editOperation, model.getUser().getId());
		foundAccountAndCategory(model);
		if(OperationType.INCOME.equals(operationType)) {
			subtractFromAccountBalance(editOperation);
			fillOperationFromModel(editOperation, model);
			addToAccountBalance(editOperation);
		} else if(OperationType.OUTCOME.equals(operationType)) {
			addToAccountBalance(editOperation);
			fillOperationFromModel(editOperation, model);
			subtractFromAccountBalance(editOperation);
		}
	}
	
	@Transactional(readOnly = true)
	public Operation findById(int userId, int operationId, Class<? extends Operation> itemClass) {
		Operation operation = Optional.ofNullable(entityManager.find(itemClass, operationId)).get();
		checkUsersRights(operation, userId);
		return operation;
	}
	
	@Transactional(readOnly = true)
	public List<? extends Operation> findAll(Operation filterInstance, DateRange dateRange) {
		return findAllByUser(filterInstance)
				.stream()
				.filter(o -> operationInDateInterval(dateRange==null?defaultDateRange():dateRange, o)
						&&operationIncludeCategory(filterInstance, o)
						&&operationIncludeCashAccount(filterInstance, o))
				.sorted((o1, o2) -> {
					int dateCompare = o1.getDate().compareTo(o2.getDate());
					if (dateCompare != 0) {
						return dateCompare;
					}
					return o1.getId() - o2.getId();
				})
				.toList();
	}
	
	private List<? extends Operation> findAllByUser(Operation filterInstatnce) {
		User user = userService.findById(filterInstatnce.getUser().getId());
		if(OperationType.INCOME.equals(filterInstatnce.getType())) {
			return user.getIncomes(); 
		} else if(OperationType.OUTCOME.equals(filterInstatnce.getType())) {
			return user.getOutcomes();
		}
		throw new IllegalArgumentException("Non correct operation type");
	}
	private void subtractFromAccountBalance(Operation operation) {
		CashAccount account = operation.getCashAccount();
		BigDecimal accountBalance = account.getBalance();
		account.setBalance(accountBalance.subtract(operation.getAmount()));
	}
	private void addToAccountBalance(Operation operation) {
		CashAccount cashAccount = operation.getCashAccount();
		BigDecimal accountBalance = cashAccount.getBalance();
		cashAccount.setBalance(accountBalance.add(operation.getAmount()));
	}
	private void fillOperationFromModel(Operation operation, Operation model) {
		operation.setAmount(model.getAmount());
		operation.setCashAccount(model.getCashAccount());
		operation.setCategory(model.getCategory());
		operation.setComment(model.getComment());
		operation.setDate(model.getDate());
	}
	private void foundAccountAndCategory(Operation model) {
		CashAccount account = model.getCashAccount();
		Category category = model.getCategory();
		category.setUser(model.getUser());
		model.setCashAccount(accountsService.findByNameAndUser(account.getName(), model.getUser()).get());
		model.setCategory(categoriesService.findInDB(category).get());
	}
	private void checkUsersRights(Operation operation, int userId) {
		if(operation.getUser().getId()!=userId) {
			throw new ForbiddenUsersActionException();
		}
	}
	private DateRange defaultDateRange() {
		return new DateRange(new GregorianCalendar(1900,0,1).getTime(), new Date());
	}
	private boolean operationInDateInterval(DateRange dateRange, Operation operation) {
		Date operationDate = operation.getDate();
		return operationDate.compareTo(dateRange.getStartDate()) >= 0
				&& operationDate.compareTo(dateRange.getEndDate()) <= 0;
	}
	private boolean operationIncludeCategory(Operation filter, Operation comparedOperation) {
		if (filter.getCategory()==null || filter.getCategory().getName()==null || comparedOperation.getCategory().getName().equals(filter.getCategory().getName())) {
			return true;
		}
		return false;
	}

	private boolean operationIncludeCashAccount(Operation filter, Operation comparedOperation) {
		if (filter.getCashAccount()==null||filter.getCashAccount().getName()==null||comparedOperation.getCashAccount().getName().equals(filter.getCashAccount().getName())) {
			return true;
		}
		return false;
	}
	
}
