package ru.ochkasovap.homeAccountingRest.models;


import jakarta.persistence.*;
import ru.ochkasovap.homeAccountingRest.util.OperationType;
import ru.ochkasovap.homeAccountingRest.util.exceptions.HomeAccountingException;

import java.math.BigDecimal;



@Entity
@Table(name = "incomes")
public class Income extends Operation {
	
	@Column(name = "income")
	private BigDecimal amount;
	
	@ManyToOne
	@JoinColumn(name = "category_id")
	private IncomeCategory category;

	public static Builder builder() {
		return new Builder(new Income());
	}
	
	public Income() {
	}
	
	@Override
	public BigDecimal getAmount() {
		return this.amount;
	}
	
	@Override
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Override
	public OperationType getType() {
		return OperationType.INCOME;
	}
	@Override
	public Category getCategory() {
		return category;
	}
	@Override
	public void setCategory(Category category) {
		if(category instanceof IncomeCategory) {
			this.category = (IncomeCategory)category;
		} else {
			throw new HomeAccountingException(new ClassCastException());
		}
	}
	
}