package ru.ochkasovap.homeAccountingRest.models;


import jakarta.persistence.*;
import ru.ochkasovap.homeAccountingRest.util.Category;
import ru.ochkasovap.homeAccountingRest.util.DateUtil;
import ru.ochkasovap.homeAccountingRest.util.Operation;
import ru.ochkasovap.homeAccountingRest.util.OperationType;

import java.math.BigDecimal;
import java.util.Date;

/**
 * The persistent class for the outcomes database table.
 * 
 */
@Entity
@Table(name = "outcomes")
public class Outcome implements Operation {

	@Id
	@SequenceGenerator(name = "outcome_gen", sequenceName = "outcomes_id_seq", initialValue = 1, allocationSize = 1)
	@GeneratedValue(generator = "outcome_gen", strategy = GenerationType.SEQUENCE)
	private int id;

	private String comment;

	private Date date;
	
	@Column(name = "outcome")
	private BigDecimal amount;

	@ManyToOne
	@JoinColumn(name = "cash_account_id")
	private CashAccount cashAccount;

	
	@ManyToOne
	@JoinColumn(name = "category_id")
	private OutcomeCategory category;

	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	public Outcome() {
	}
	
	public static class Builder {
		private Outcome outcome = new Outcome();

		public Outcome build() {
			try{return outcome;}
			finally {outcome = new Outcome();}
		}

		public Builder id(int id) {
			outcome.setId(id);
			return this;
		}

		public Builder comment(String comment) {
			outcome.setComment(comment);
			return this;
		}

		public Builder date(Date date) {
			outcome.setDate(date);
			return this;
		}

		public Builder amount(BigDecimal amount) {
			outcome.setAmount(amount);
			return this;
		}

		public Builder cashAccount(CashAccount cashAccount) {
			outcome.setCashAccount(cashAccount);
			return this;
		}

		public Builder category(OutcomeCategory category) {
			outcome.setCategory(category);
			return this;
		}

		public Builder user(User user) {
			outcome.setUser(user);
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
		return category;
	}

	public void setCategory(OutcomeCategory category) {
		this.category = category;
	}
	
	@Override
	public void setCategory(Category category) {
		if (category instanceof OutcomeCategory) {
			setCategory((OutcomeCategory) category);
		}
		
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public OperationType getType() {
		return OperationType.OUTCOME;
	}

	@Override
	public String toString() {
		return "Расход [дата=" + DateUtil.convertDateToString(date) + ", расход = " + amount + ", счет: " + cashAccount.getName() + ", категория: "
				+ category.getName() + ", комментарий: "
						+ comment +"]";
	}
	
	
}