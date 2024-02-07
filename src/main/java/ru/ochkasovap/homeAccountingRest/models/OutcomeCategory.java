package ru.ochkasovap.homeAccountingRest.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.ochkasovap.homeAccountingRest.util.OperationType;

@Entity
@Table(name = "outcome_category")
public class OutcomeCategory extends Category {
	
	public static Builder builder() {
		return new Builder(new OutcomeCategory());
	}

	public OutcomeCategory() {
	}
	
	public OutcomeCategory(
			@NotNull(message = "Поле не должно быть пустым") @Size(min = 1, message = "Поле не должно быть пустым") String name) {
		super();
		this.setName(name);
	}

	@Override
	public OperationType getType() {
		return OperationType.OUTCOME;
	}
	

}