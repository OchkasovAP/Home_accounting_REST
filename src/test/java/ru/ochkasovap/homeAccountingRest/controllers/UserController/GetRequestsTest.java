package ru.ochkasovap.homeAccountingRest.controllers.UserController;


import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.minidev.json.JSONArray;
import net.minidev.json.parser.JSONParser;
import ru.ochkasovap.homeAccountingRest.dto.UserDTO;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Sql(value = {"/update-users-after-test.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = {"/CreateTableScript.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class GetRequestsTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	
	private UserDTO adminDTO;
	private UserDTO userDTO;
	
	@BeforeEach
	void setUp() {
		adminDTO = UserDTO.builder().id(1).login("admin").role("ADMIN").build();
		userDTO = UserDTO.builder().id(2).login("user").role("USER").build();
	}
	
	@Test
	@WithUserDetails("admin")
	void showUsers_AdminRole() throws Exception {
		JSONParser jsonParser= new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
		JSONArray listJson = (JSONArray) jsonParser.parse(objectMapper.writeValueAsString(List.of(adminDTO, userDTO)));
		mockMvc.perform(get("/users"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$").value(Matchers.containsInAnyOrder(listJson.toArray())));
	}
	@Test
	void showUsers_NonAuthenticated() throws Exception {
		mockMvc.perform(get("/users"))
		.andExpect(status().is4xxClientError());
	}
	
	@Test
	@WithUserDetails("user")
	void showUsers_UserRole() throws Exception {
		mockMvc.perform(get("/users"))
		.andExpect(status().is4xxClientError());
	}
	@Test
	@WithUserDetails("admin")
	void userInfo_AdminRole() throws Exception {
		for(UserDTO user:new UserDTO[] {userDTO, adminDTO}) {
			performGetUserInfo(user);
		}
	}
	@Test 
	@WithUserDetails("user")
	void userInfo_UserRole() throws Exception {
		mockMvc.perform(get("/users/{id}", 1L))
		.andExpect(status().is4xxClientError());
		
		performGetUserInfo(userDTO);
	}
	void performGetUserInfo(UserDTO user) throws Exception{
		mockMvc.perform(get("/users/{id}", user.getId()))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id").value(user.getId()))
		.andExpect(jsonPath("$.login").value(user.getLogin()))
		.andExpect(jsonPath("$.role").value(user.getRole()));
	}
	@Test
	void userInfo_NonAuthenticated() throws Exception{
		mockMvc.perform(get("/users/{id}", 1L))
		.andExpect(status().is4xxClientError());
		mockMvc.perform(get("/users/{id}", 2L))
		.andExpect(status().is4xxClientError());
	}
}
