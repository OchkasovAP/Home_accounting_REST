package ru.ochkasovap.homeAccountingRest.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegistrationDTO {
	
	@NotEmpty(message = "Поле не должно быть пустым")
	@Size(max = 20, message = "Поле не должно превышать 20 символов")
	private String login;
	
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,20}$", message = "Пароль должен иметь 1 цифру, 1 строчную, 1 прописную латинскую букву и быть размером не 6-20 символов")
	@NotNull(message = "Поле не должно быть пустым")
	private String password;
	
	@NotNull(message = "Поле не должно быть пустым")
	private String repeatedNewPassword;
	
	public static class Builder {
		private RegistrationDTO registrationDTO;
		private Builder(String login, String password) {
			registrationDTO = new RegistrationDTO();
			registrationDTO.setLogin(login);
			registrationDTO.setPassword(password);
		}
		public RegistrationDTO build() {
			return registrationDTO;
		}
	
		public Builder repeatedNewPassword(String repeatedNewPassword) {
			registrationDTO.setRepeatedNewPassword(repeatedNewPassword);
			return this;
		}
		
	}
	public static Builder builder(String login, String password) {
		return new Builder(login, password);
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

	public String getRepeatedNewPassword() {
		return repeatedNewPassword;
	}

	public void setRepeatedNewPassword(String repeatedNewPassword) {
		this.repeatedNewPassword = repeatedNewPassword;
	}

}
