package ru.ochkasovap.homeAccountingRest.dto;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AccountDTO {
	private int id;
	
	private BigDecimal balance;

	private boolean containInGenBalance;
	
	@NotEmpty(message = "Поле не должно быть пустым")
	@Size(max = 20, message = "Поле не должно превышать 20 символов")
	private String name;
	
	public static class Builder {
		private AccountDTO accountDTO;
		public AccountDTO build() {
			return accountDTO;
		}
		private Builder() {
			accountDTO = new AccountDTO();
		}
		public Builder id(int id) {
			accountDTO.setId(id);
			return this;
		}
		public Builder balance(BigDecimal balance) {
			accountDTO.setBalance(balance);
			return this;
		}
		public Builder containInGenBalance(boolean containInGenBalance) {
			accountDTO.setContainInGenBalance(containInGenBalance);
			return this;
		}
		public Builder name(String name) {
			accountDTO.setName(name);
			return this;
		}
		
	}
	
	public static Builder bulider(){
		return new Builder();
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public boolean isContainInGenBalance() {
		return containInGenBalance;
	}

	public void setContainInGenBalance(boolean containInGenBalance) {
		this.containInGenBalance = containInGenBalance;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
}
