package ru.ochkasovap.homeAccountingRest.util.exceptions;

public class ForbiddenUsersActionException extends HomeAccountingException{

	
	public ForbiddenUsersActionException() {
		super("Пользователь не обладает правами совершать это действие");
		
	}

	public ForbiddenUsersActionException(String message) {
		super(message);
	}

}
