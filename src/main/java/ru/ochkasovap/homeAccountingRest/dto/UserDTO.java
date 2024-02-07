package ru.ochkasovap.homeAccountingRest.dto;


public class UserDTO {
	
	private int id;
	
	private String login;
	
	private String role;
	
	public static class Builder {
		private UserDTO userDTO;
		
		private Builder() {
			userDTO = new UserDTO();
		}

		public UserDTO build() {
			return userDTO;
		}

		public Builder id(int id) {
			userDTO.setId(id);
			return this;
		}

		public Builder login(String login) {
			userDTO.setLogin(login);
			return this;
		}

		public Builder role(String role) {
			userDTO.setRole(role);
			return this;
		}
		
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}

	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	
	
	
}
