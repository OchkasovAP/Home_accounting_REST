package ru.ochkasovap.homeAccountingRest.util;

import java.util.List;

import ru.ochkasovap.homeAccountingRest.models.User;



public interface Category {
	String getName();
	void setName(String name);
	
	User getUser();
	void setUser(User user);

	Integer getId();
	void setId(Integer id);

	OperationType getType();
	<T extends Operation> List<T> getOperations();
	
}
