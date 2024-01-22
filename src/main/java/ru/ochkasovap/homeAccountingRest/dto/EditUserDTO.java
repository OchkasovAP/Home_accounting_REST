package ru.ochkasovap.homeAccountingRest.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import ru.ochkasovap.homeAccountingRest.dto.RegistrationDTO.Builder;

public class EditUserDTO {
	private int id;
	
	@NotEmpty(message = "Поле не должно быть пустым")
	@Size(max = 20, message = "Поле не должно превышать 20 символов")
	private String login;
	
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,20}$", message = "Пароль должен иметь 1 цифру, 1 строчную, 1 прописную латинскую букву и быть размером не 6-20 символов")
	private String password;
	
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,20}$", message = "Пароль должен иметь 1 цифру, 1 строчную, 1 прописную латинскую букву и быть размером не 6-20 символов")
	private String newPassword;
	
	private String repeatedNewPassword;
	
	private String role;
	
	public static class Builder {
		private EditUserDTO editUserDTO;
		private Builder(int id, String login) {
			editUserDTO = new EditUserDTO();
			editUserDTO.setId(id);
			editUserDTO.setLogin(login);
		}
		public EditUserDTO build() {
			return editUserDTO;
		}
		public Builder password(String password) {
			editUserDTO.setPassword(password);
			return this;
		}
		public Builder newPassword(String newPassword) {
			editUserDTO.setNewPassword(newPassword);
			return this;
		}
		public Builder repeatedNewPassword(String repeatedNewPassword) {
			editUserDTO.setRepeatedNewPassword(repeatedNewPassword);
			return this;
		}
		public Builder role(String role) {
			editUserDTO.setRole(role);
			return this;
		}
		
	}
	public static Builder builder(int id, String login) {
		return new Builder(id, login);
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getRepeatedNewPassword() {
		return repeatedNewPassword;
	}

	public void setRepeatedNewPassword(String repeatedNewPassword) {
		this.repeatedNewPassword = repeatedNewPassword;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
