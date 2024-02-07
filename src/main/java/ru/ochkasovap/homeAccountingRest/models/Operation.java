package ru.ochkasovap.homeAccountingRest.models;

import java.math.BigDecimal;
import java.util.Date;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import ru.ochkasovap.homeAccountingRest.util.OperationType;

@MappedSuperclass
public abstract class Operation extends AbstractModel{

	private String comment;

	private Date date;

	@ManyToOne
	@JoinColumn(name = "cash_account_id")
	private CashAccount cashAccount;


	@ManyToOne
	private User user;
	
	public static class Builder {
		private Operation operation;
		
		public Builder(Operation operation) {
			this.operation = operation;
		}

		public Operation build() {
			return operation;
		}

		public Builder id(int id) {
			operation.setId(id);
			return this;
		}

		public Builder comment(String comment) {
			operation.setComment(comment);
			return this;
		}

		public Builder date(Date date) {
			operation.setDate(date);
			return this;
		}

		public Builder amount(BigDecimal amount) {
			operation.setAmount(amount);
			return this;
		}

		public Builder cashAccount(CashAccount cashAccount) {
			operation.setCashAccount(cashAccount);
			return this;
		}

		public Builder category(Category category) {
			operation.setCategory(category);
			return this;
		}

		public Builder user(User user) {
			operation.setUser(user);
			return this;
		}
		
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public CashAccount getCashAccount() {
		return cashAccount;
	}

	public void setCashAccount(CashAccount cashAccount) {
		this.cashAccount = cashAccount;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	public abstract OperationType getType();
	
	public abstract BigDecimal getAmount();
	public abstract void setAmount(BigDecimal amount);
	
	public abstract Category getCategory();
	public abstract void setCategory(Category category);
}
