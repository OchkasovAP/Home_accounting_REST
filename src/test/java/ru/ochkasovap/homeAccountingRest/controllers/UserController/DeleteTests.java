package ru.ochkasovap.homeAccountingRest.controllers.UserController;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Sql(value = {"/CreateTableScript.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class DeleteTests {
	@Autowired
	private MockMvc mockMvc;
	
	@Test
	@WithUserDetails("admin")
	void delete_AdminRole() throws Exception {
		mockMvc.perform(delete("/users/{id}", 2))
		.andExpect(status().isNoContent());
		
		mockMvc.perform(get("/users/{id}", 2))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.Exception").value("Пользователь с таким id не существует"));
	}
	@Test
	@WithUserDetails("admin")
	void delete_AdminRole_DeleteYourself() throws Exception {
		mockMvc.perform(delete("/users/{id}", 1))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.Exception").value("Администратор не может удалить самого себя"));
	}
	@Test
	@WithUserDetails("user")
	void delete_UserRole() throws Exception {
		mockMvc.perform(delete("/users/{id}", 1))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.Exception").value("Пользователь в правами 'User' не может удалять других пользователей"));
	}
	@Test
	@WithUserDetails("user")
	void delete_UserRole_DeleteYourSelf() throws Exception {
		mockMvc.perform(delete("/users/{id}", 2))
		.andExpect(status().isNoContent());
	}
	
	@Test
	void delete_NonAuthenticated() throws Exception {
		mockMvc.perform(delete("/users/{id}", 1))
		.andExpect(status().isForbidden());

	}
	
}
