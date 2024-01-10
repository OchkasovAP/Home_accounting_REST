package ru.ochkasovap.homeAccountingRest.services;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import ru.ochkasovap.homeAccountingRest.models.IncomeCategory;
import ru.ochkasovap.homeAccountingRest.models.OutcomeCategory;
import ru.ochkasovap.homeAccountingRest.models.User;
import ru.ochkasovap.homeAccountingRest.util.Category;
import ru.ochkasovap.homeAccountingRest.util.OperationType;
import ru.ochkasovap.homeAccountingRest.util.exceptions.ForbiddenUsersActionException;


@Service
@Transactional(readOnly = true)
public class CategoriesService {
	@Autowired
	private UserService userService;
	@Autowired
	private EntityManager entityManager;
	
	public List<? extends Category> findAllByUser(int userID, OperationType type) {
		User user = userService.findById(userID);
		List<? extends Category> categories = Collections.emptyList();
		if(OperationType.INCOME.equals(type)) {
			categories = user.getIncomeCategories();
		} else if(OperationType.OUTCOME.equals(type)) {
			categories = user.getOutcomeCategories();
		}
		return categories.stream().sorted(Comparator.comparing(c -> c.getId())).toList();
	}

	public Category findById(int userId, int id, Class<? extends Category> itemClass) {
		Category category = entityManager.find(itemClass, id);
		checkUsersRights(category, userId);
		return category;
	}
	
	public Optional<Category> findInDB(Category category) {
		Class<Category> itemClass = category.getType().getCategoryClass();
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Category> criteriaQuery = criteriaBuilder.createQuery(itemClass);
		Root<Category> root = criteriaQuery.from(itemClass);
		criteriaQuery.select(root)
		.where(criteriaBuilder
				.and(criteriaBuilder.equal(root.get("name"), category.getName()),
					criteriaBuilder.equal(root.get("user"), category.getUser())));
		return entityManager.createQuery(criteriaQuery).getResultStream().findAny();
	}
	
	@Transactional
	public void edit(Category category) {
		Category categoryFromDB = entityManager.find(category.getClass(), category.getId());
		checkUsersRights(categoryFromDB, category.getUser().getId());
		categoryFromDB.setName(category.getName());
	}
	@Transactional
	public void create(int userID, Category category) {
		User user = userService.findById(userID);
		if (category instanceof IncomeCategory) {
			user.getIncomeCategories().add((IncomeCategory) category);
		} else if (category instanceof OutcomeCategory) {
			user.getOutcomeCategories().add((OutcomeCategory) category);
		}
		category.setUser(user);
	}
	@Transactional
	public void remove(int userId, int categoryID, OperationType type) {
		Category category = entityManager.find(type.getCategoryClass(), categoryID);
		User user = category.getUser();
		checkUsersRights(category, userId);
		if (OperationType.INCOME.equals(type)) {
			user.getIncomeCategories().remove((IncomeCategory)category);
		} else if (OperationType.OUTCOME.equals(type)) {
			user.getOutcomeCategories().remove((OutcomeCategory)category);
		}
		category.setUser(null);
	}
	private void checkUsersRights(Category category, int userId) {
		if(category.getUser().getId()!=userId) {
			throw new ForbiddenUsersActionException();
		}
	}

}
