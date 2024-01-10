package ru.ochkasovap.homeAccountingRest.models;


import jakarta.persistence.*;
import ru.ochkasovap.homeAccountingRest.util.Category;
import ru.ochkasovap.homeAccountingRest.util.DateUtil;
import ru.ochkasovap.homeAccountingRest.util.Operation;
import ru.ochkasovap.homeAccountingRest.util.OperationType;

import java.math.BigDecimal;
import java.util.Date;


@Entity
@Table(name = "incomes")
public class Income implements Operation {

	@Id
	@SequenceGenerator(name = "income_gen", sequenceName = "incomes_id_seq", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "income_gen", strategy = GenerationType.SEQUENCE)
	private int id;

	private String comment;

	private Date date;
	
	@Column(name = "income")
	private BigDecimal amount;

	@ManyToOne
	@JoinColumn(name = "cash_account_id")
	private CashAccount cashAccount;

	@ManyToOne
	@JoinColumn(name = "category_id")
	private IncomeCategory category;

	@ManyToOne
	private User user;

	public Income() {
	}
	
	public static class Builder {
		private Income income = new Income();

		public Income build() {
			try{return income;}
			finally {income = new Income();}
		}

		public Builder id(int id) {
			income.setId(id);
			return this;
		}

		public Builder comment(String comment) {
			income.setComment(comment);
			return this;
		}

		public Builder date(Date date) {
			income.setDate(date);
			return this;
		}

		public Builder amount(BigDecimal amount) {
			income.setAmount(amount);
			return this;
		}

		public Builder cashAccount(CashAccount cashAccount) {
			income.setCashAccount(cashAccount);
			return this;
		}

		public Builder category(IncomeCategory category) {
			income.setCategory(category);
			return this;
		}

		public Builder user(User user) {
			income.setUser(user);
			return this;
		}
		
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getComment() {
		return this.comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public CashAccount getCashAccount() {
		return this.cashAccount;
	}

	public void setCashAccount(CashAccount cashAccount) {
		this.cashAccount = cashAccount;
	}

	public Category getCategory() {
		return this.category;
	}

	public void setCategory(IncomeCategory category) {
		this.category = category;
	}

	@Override
	public void setCategory(Category category) {
		if (category instanceof IncomeCategory) {
			setCategory((IncomeCategory) category);
		}
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "Доход [дата=" + DateUtil.convertDateToString(date) + ", доход = " + amount + ", счет: " + cashAccount.getName() + ", категория: "
				+ category.getName() + ", комментарий: "
						+ comment +"]";
	}

	@Override
	public OperationType getType() {
		return OperationType.INCOME;
	}
	
}