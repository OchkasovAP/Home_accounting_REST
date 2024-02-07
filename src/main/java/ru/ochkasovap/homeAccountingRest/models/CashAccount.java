package ru.ochkasovap.homeAccountingRest.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Entity
@Table(name = "cash_account")
public class CashAccount extends AbstractModel{
	
	@NotNull
	private BigDecimal balance;

	@Column(name = "contain_in_gen_balance")
	private boolean containInGenBalance;
	
	@NotNull(message = "Поле не должно быть пустым")
	@Size(min = 1,message = "Поле не должно быть пустым")
	private String name;

	@ManyToOne
	private User user;

	public CashAccount() {
	}
	
 	public static class Builder {
		private CashAccount cashAccount = new CashAccount();

		public CashAccount build() {
			try{return cashAccount;}
			finally {cashAccount = new CashAccount();}
		}

		public Builder id(Integer id) {
			cashAccount.setId(id);
			return this;
		}

		public Builder balance(BigDecimal balance) {
			cashAccount.setBalance(balance);
			return this;
		}

		public Builder containInGenBalance(Boolean containInGenBalance) {
			cashAccount.setContainInGenBalance(containInGenBalance);
			return this;
		}

		public Builder name(String name) {
			cashAccount.setName(name);
			return this;
		}

		public Builder user(User user) {
			cashAccount.setUser(user);
			return this;
		}
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public BigDecimal getBalance() {
		return this.balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public Boolean getContainInGenBalance() {
		return this.containInGenBalance;
	}

	public void setContainInGenBalance(Boolean containInGenBalance) {
		this.containInGenBalance = containInGenBalance;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return String.format("Имя - %s, баланс - %s, учитывается в общем балансе - %s", name, balance, containInGenBalance?"да":"нет");
	}
	
}