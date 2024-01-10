package ru.ochkasovap.homeAccountingRest.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.ochkasovap.homeAccountingRest.util.Category;
import ru.ochkasovap.homeAccountingRest.util.Operation;
import ru.ochkasovap.homeAccountingRest.util.OperationType;

import java.util.List;


@Entity
@Table(name = "outcome_category")
public class OutcomeCategory implements Category {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@NotEmpty(message = "Поле не должно быть пустым")
	@Size(min = 1,message = "Поле не должно быть пустым")
	private String name;

	@ManyToOne
	private User user;

	@OneToMany(mappedBy = "category")
	private List<Outcome> outcomes;

	public OutcomeCategory() {
	}
	
	public OutcomeCategory(
			@NotNull(message = "Поле не должно быть пустым") @Size(min = 1, message = "Поле не должно быть пустым") String name) {
		super();
		this.name = name;
	}

	public static class Builder {
		private OutcomeCategory category = new OutcomeCategory();

		public OutcomeCategory build() {
			try{return category;}
			finally {category = new OutcomeCategory();}
		}

		public Builder id(Integer id) {
			category.setId(id);
			return this;
		}

		public Builder name(String name) {
			category.setName(name);
			return this;
		}

		public Builder user(User user) {
			category.setUser(user);
			return this;
		}
		public Builder outcomes(List<Outcome> outcomes) {
			category.setOutcomes(outcomes);
			return this;
		}
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Outcome> getOutcomes() {
		return this.outcomes;
	}

	public void setOutcomes(List<Outcome> outcomes) {
		this.outcomes = outcomes;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Operation> List<T> getOperations() {
		return (List<T>) getOutcomes();
	}


	@Override
	public OperationType getType() {
		return OperationType.OUTCOME;
	}
	

}