package ru.ochkasovap.homeAccountingRest.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;



@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@Column(name = "login")
	@NotNull(message = "Поле не должно быть пустым")
	@Size(min = 1, message = "Поле не должно быть пустым")
	private String login;
	
	private String password;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Income> incomes;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Outcome> outcomes;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CashAccount> cashAccounts;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<IncomeCategory> incomeCategories;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OutcomeCategory> outcomeCategories;

	@ManyToOne
	@JoinColumn(name = "role_id")
	private Role role;

	@Override
	public String toString() {
		return "User [login=" + login + ", cashAccounts=" + cashAccounts + ", incomeCategories=" + incomeCategories
				+ ", outcomeCategories=" + outcomeCategories + ", role=" + role + "]";
	}

	public User() {
	}
	
	public static class Builder {
		private User user = new User();
		
		public User build() {
			try {return user;}
			finally {user = new User();}
		}
		public Builder id(int id) {
			user.setId(id);
			return this;
		}
		public Builder login(String login) {
			user.setLogin(login);
			return this;
		}
		public Builder password(String password) {
			user.setPassword(password);
			return this;
		}
		public Builder incomes(List<Income> incomes) {
			user.setIncomes(incomes);
			return this;
		}
		public Builder outcomes(List<Outcome> outcomes) {
			user.setOutcomes(outcomes);
			return this;
		}
		public Builder cashAccounts(List<CashAccount> cashAccounts) {
			user.setCashAccounts(cashAccounts);
			return this;
		}
		public Builder incomeCategories(List<IncomeCategory> incomeCategories) {
			user.setIncomeCategories(incomeCategories);
			return this;
		}
		public Builder outcomeCategories(List<OutcomeCategory> outcomeCategories) {
			user.setOutcomeCategories(outcomeCategories);
			return this;
		}
		public Builder role(Role role) {
			user.setRole(role);
			return this;
		}
	}
	
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLogin() {
		return this.login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<Income> getIncomes() {
		return this.incomes;
	}
	
	public void setIncomes(List<Income> incomes) {
		this.incomes = incomes;
	}

	public List<Outcome> getOutcomes() {
		return this.outcomes;
	}


	public void setOutcomes(List<Outcome> outcomes) {
		this.outcomes = outcomes;
	}

	public List<CashAccount> getCashAccounts() {
		return this.cashAccounts;
	}

	public void setCashAccounts(List<CashAccount> cashAccounts) {
		this.cashAccounts = cashAccounts;
	}

	public List<IncomeCategory> getIncomeCategories() {
		return incomeCategories;
	}

	public void setIncomeCategories(List<IncomeCategory> incomeCategories) {
		this.incomeCategories = incomeCategories;
	}

	public List<OutcomeCategory> getOutcomeCategories() {
		return outcomeCategories;
	}

	public void setOutcomeCategories(List<OutcomeCategory> outcomeCategories) {
		this.outcomeCategories = outcomeCategories;
	}

	public Role getRole() {
		return this.role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public boolean isAdmin() {
		return role.getName().equals(Role.ADMIN);
	}
}