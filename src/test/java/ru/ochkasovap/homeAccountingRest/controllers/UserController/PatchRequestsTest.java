package ru.ochkasovap.homeAccountingRest.controllers.UserController;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.ochkasovap.homeAccountingRest.dto.EditUserDTO;
import ru.ochkasovap.homeAccountingRest.dto.RegistrationDTO;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Sql(value = {"/update-users-after-test.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = {"/CreateTableScript.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class PatchRequestsTest {
	
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	
	private EditUserDTO editUser;
	
	@BeforeEach
	void setUp() {
		editUser = EditUserDTO.builder(2, "user").role("ADMIN").build();
	}
	
	@Test
	void edit_NonAuthenticated() throws Exception {
		mockMvc.perform(patch("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(editUser)))
		.andExpect(status().isForbidden());
	}
	
	@Test
	@WithUserDetails("user")
	void edit_UserRole() throws Exception {
		performPatchStatusOk();
		
		editUser.setLogin("TestUser");
		performPatchStatusOk();
		
		editUser.setPassword("Test12345");
		editUser.setNewPassword("Test54321");
		editUser.setRepeatedNewPassword("Test54321");
		performPatchStatusOk();
		
		
		mockMvc.perform(get("/users/{id}", 2))
		.andExpect(jsonPath("$.id").value(2))
		.andExpect(jsonPath("$.login").value("TestUser"))
		.andExpect(jsonPath("$.role").value("USER"));
		
		editUser.setPassword("Test54321");
		mockMvc.perform(post("/users/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(editUser)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.jwt_token").exists());
	}
	@Test
	@WithUserDetails("admin")
	void edit_AdminRole() throws Exception {
		editUser.setId(1);
		editUser.setLogin("admin");
		editUser.setRole("USER");
		mockMvc.perform(patch("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(editUser)))
		.andExpect(status().isOk());
		
		editUser.setLogin("TestAdmin");
		mockMvc.perform(patch("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(editUser)))
		.andExpect(status().isOk());
		
		editUser.setNewPassword("Test54321");
		editUser.setRepeatedNewPassword("Test54321");
		mockMvc.perform(patch("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(editUser)))
		.andExpect(status().isOk());
		editUser.setPassword("Test54321");
		
		mockMvc.perform(get("/users/{id}", 1))
		.andExpect(jsonPath("$.id").value(1))
		.andExpect(jsonPath("$.login").value("TestAdmin"))
		.andExpect(jsonPath("$.role").value("ADMIN"));
		
		mockMvc.perform(post("/users/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(editUser)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.jwt_token").exists());
	}
	@Test
	@WithUserDetails("admin")
	void edit_AdminRoleAnotherUser() throws Exception {
		editUser.setLogin("TestUser");
		mockMvc.perform(patch("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(editUser)))
		.andExpect(status().isOk());
	}
	@Test
	@WithUserDetails("user")
	void edit_UserRoleAnotherUser() throws Exception {
		editUser.setId(1);
		mockMvc.perform(patch("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(editUser)))
		.andExpect(status().isForbidden());
	}
	
	@Test
	@WithUserDetails("user")
	void edit_UserRoleFailValid() throws Exception {
		editUser.setLogin("admin");
		edit_EditRoleFailValid("login","Пользователь с таким именем уже существует");
		
		editUser.setLogin("user");
		editUser.setPassword("Test1234");
		editUser.setNewPassword("Test1234");
		editUser.setRepeatedNewPassword("Test1234");
		edit_EditRoleFailValid("password","Неверный текущий пароль");
	
		editUser.setRepeatedNewPassword("Test");
		editUser.setPassword("Test12345");
		edit_EditRoleFailValid("repeatedNewPassword","Неверно повторен пароль");
		
		String[] emptyPasswords = new String[] {null, "", "  "};
		for(String emptyPassword: emptyPasswords) {
			editUser.setRepeatedNewPassword(emptyPassword);
			edit_EditRoleFailValid("repeatedNewPassword","Поле не должно быть пустым");
		}
		editUser.setRepeatedNewPassword("Test");
		editUser.setNewPassword(null);
		edit_EditRoleFailValid("newPassword","Поле не должно быть пустым");
		
		for(String password:new String[] {"TestedPassword","Test1", "VeryVeryVeryLongPassword12345"}) {
			editUser.setNewPassword(password);
			editUser.setRepeatedNewPassword(password);
			edit_EditRoleFailValid("newPassword", "Пароль должен иметь 1 цифру, 1 строчную, 1 прописную латинскую букву и быть размером не 6-20 символов");
		}
	}
	void edit_EditRoleFailValid(String field, String exceptionMessage) throws Exception {
		mockMvc.perform(patch("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(editUser)))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.Exception").value("Field - "+field+", error - "+exceptionMessage+";"));

	}
	void performPatchStatusOk() throws Exception{
		mockMvc.perform(patch("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(editUser)))
		.andExpect(status().isOk());
	}
	
}
