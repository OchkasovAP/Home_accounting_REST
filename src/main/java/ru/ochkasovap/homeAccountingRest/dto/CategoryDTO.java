package ru.ochkasovap.homeAccountingRest.dto;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;


public class CategoryDTO {
	private int id;
	
	@NotEmpty(message = "Поле не должно быть пустым")
	@Size(max = 20, message = "Поле не должно превышать 20 символов")
	private String name;
	
	public static class Builder {
		private CategoryDTO category;
		private Builder() {
			category = new CategoryDTO();
		}
		public CategoryDTO build() {
			return category;
		}
		public Builder id(int id) {
			category.setId(id);
			return this;
		}
		public Builder name(String name) {
			category.setName(name);
			return this;
		}
		
	}
	public static Builder builder(){
		return new Builder();
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
