package ru.ochkasovap.homeAccountingRest.models;


import jakarta.persistence.*;
import ru.ochkasovap.homeAccountingRest.util.OperationType;
import ru.ochkasovap.homeAccountingRest.util.exceptions.HomeAccountingException;

import java.math.BigDecimal;


@Entity
@Table(name = "outcomes")
public class Outcome extends Operation {

	@Column(name = "outcome")
	private BigDecimal amount;
	
	@ManyToOne
	@JoinColumn(name = "category_id")
	private OutcomeCategory category;

	public static Builder builder() {
		return new Builder(new Outcome());
	}	

	public Outcome() {
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
		return OperationType.OUTCOME;
	}
	
	@Override
	public Category getCategory() {
		return category;
	}
	@Override
	public void setCategory(Category category) {
		if(category instanceof OutcomeCategory) {
			this.category = (OutcomeCategory)category;
		} else {
			throw new HomeAccountingException(new ClassCastException());
		}
	}

}