package ru.ochkasovap.homeAccountingRest.models;


import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import ru.ochkasovap.homeAccountingRest.util.OperationType;

@MappedSuperclass
public abstract class Category extends AbstractModel{
	
	@NotEmpty(message = "Поле не должно быть пустым")
	@Size(min = 1,message = "Поле не должно быть пустым")
	private String name;
	
	@ManyToOne
	private User user;
	

	public static class Builder {
		private Category category;
		
		public Builder(Category category) {
			this.category = category;
		}
		
		public Category build() {
			return category;
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

	}

	public String getName() {
		return name;
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
	

	public abstract OperationType getType();
	
}
