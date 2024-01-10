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
@Table(name="income_category")
public class IncomeCategory implements Category {

	@Id
	@GeneratedValue(strategy =GenerationType.IDENTITY )
	private int id;
	
	@NotEmpty(message = "Поле не должно быть пустым")
	@Size(min = 1,message = "Поле не должно быть пустым")
	private String name;
	
	@ManyToOne
	private User user;

	@OneToMany(mappedBy="category")
	private List<Income> incomes;

	public IncomeCategory() {
	}
	
	public IncomeCategory(
			@NotNull(message = "Поле не должно быть пустым") @Size(min = 1, message = "Поле не должно быть пустым") String name) {
		super();
		this.name = name;
	}


	public static class Builder {
		private IncomeCategory category = new IncomeCategory();

		public IncomeCategory build() {
			try{return category;}
			finally {category = new IncomeCategory();}
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
		public Builder incomes(List<Income> incomes) {
			category.setIncomes(incomes);
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

	public List<Income> getIncomes() {
		return this.incomes;
	}

	public void setIncomes(List<Income> incomes) {
		this.incomes = incomes;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Operation> List<T> getOperations() {
		return (List<T>) getIncomes();
	}
	
	@Override
	public OperationType getType() {
		return OperationType.INCOME;
	}
	
}