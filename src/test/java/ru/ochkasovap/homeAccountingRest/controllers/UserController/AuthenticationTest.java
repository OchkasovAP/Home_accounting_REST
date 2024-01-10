package ru.ochkasovap.homeAccountingRest.controllers.UserController;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;


import ru.ochkasovap.homeAccountingRest.dto.AuthenticationDTO;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Sql(value = {"/update-users-after-test.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = {"/CreateTableScript.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class AuthenticationTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	
	private AuthenticationDTO createdUser;
	
	@BeforeEach
	void setUp() {
		createdUser = AuthenticationDTO.builder(0, "Test_User", "Test12345")
				.repeatedNewPassword("Test12345")
				.build();
	}
	
	@Test
	void login() throws Exception {
		AuthenticationDTO admin = AuthenticationDTO.builder(1, "admin", "Test12345").build();
		mockMvc.perform(post("/users/login")
		.contentType(MediaType.APPLICATION_JSON)
		.content(objectMapper.writeValueAsString(admin)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.jwt_token").exists());
	}
	@Test
	void login_NonCorrect() throws Exception {
		AuthenticationDTO admin = AuthenticationDTO.builder(1, "admin", "user").build();
		mockMvc.perform(post("/users/login")
		.contentType(MediaType.APPLICATION_JSON)
		.content(objectMapper.writeValueAsString(admin)))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.message").value("Некорректный логин или пароль"));
	}
	@Test
	void registration() throws Exception {
		AuthenticationDTO newUser = AuthenticationDTO.builder(0, "Test_User", "Test12345").repeatedNewPassword("Test12345").build();
		mockMvc.perform(post("/users/registration")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newUser)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.jwt_token").exists());
	}
	@Test
	void registration_FailLoginValid() throws Exception {
		createdUser.setLogin("admin");
		registration_PerformFailPost("login", "Пользователь с таким именем уже существует");
		for(String emptyLogin:new String[] {"", null}) {
			createdUser.setLogin(emptyLogin);
			registration_PerformFailPost("login", "Поле не должно быть пустым");
		}
		createdUser.setLogin("Very very very long login");
		registration_PerformFailPost("login", "Поле не должно превышать 20 символов");
	}
	@Test
	void registration_FailPasswordValid() throws Exception {
		createdUser.setRepeatedNewPassword("TestedPassword");
		registration_PerformFailPost("repeatedNewPassword", "Неверно повторен пароль");
		for(String password:new String[] {"TestedPassword","Test1", "VeryVeryVeryLongPassword12345"}) {
			createdUser.setPassword(password);
			createdUser.setRepeatedNewPassword(password);
			registration_PerformFailPost("password", "Пароль должен иметь 1 цифру, 1 строчную, 1 прописную латинскую букву и быть размером не 6-20 символов");
		}
		createdUser.setPassword(null);
		registration_PerformFailPost("password", "Поле не должно быть пустым");
	}
	void registration_PerformFailPost(String field, String exceptionMessage) throws Exception {
		mockMvc.perform(post("/users/registration")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(createdUser)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.Exception").value("Field - "+field+", error - "+exceptionMessage+";"));
	}
}
