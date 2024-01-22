package ru.ochkasovap.homeAccountingRest.controllers;


import org.hamcrest.Matchers;
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

import net.minidev.json.JSONArray;
import net.minidev.json.parser.JSONParser;
import ru.ochkasovap.homeAccountingRest.dto.AccountDTO;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Sql(value = {"/CreateTableScript.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class CashAccountControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	
	private AccountDTO createdAccount;
	private AccountDTO editedAccount;
	
	@BeforeEach
	void setUp() {
		editedAccount = AccountDTO.bulider()
				.id(1).name("TestAccount1")
				.balance(new BigDecimal(500))
				.containInGenBalance(true)
				.build();
		createdAccount = AccountDTO.bulider()
				.name("NewAccount")
				.balance(new BigDecimal(1000))
				.containInGenBalance(false)
				.build();
	}
	@Test
	void showCashAccounts_NonAutenticated() throws Exception{
		mockMvc.perform(get("/cashAccounts"))
		.andExpect(status().isForbidden());
	}
	@Test
	@WithUserDetails("user")
	void showCashAccounts() throws Exception {
		JSONParser jsonParser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
		JSONArray jsonArray = (JSONArray) jsonParser.parse(objectMapper.writeValueAsString(List.of(
				Map.of("id",1,"name","TestAccount1","balance",1000.0,"containInGenBalance",true),
				Map.of("id",2,"name","TestAccount2","balance",1000.0,"containInGenBalance",true)
				)));
		mockMvc.perform(get("/cashAccounts"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.generalBalance").value(2000))
		.andExpect(jsonPath("$.accounts", Matchers.containsInAnyOrder(jsonArray.toArray())));
	}
	@Test
	@WithUserDetails("user")
	void cashAccountInfo_WithDetails() throws Exception{
		mockMvc.perform(get("/cashAccounts/{id}", 1))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id").value(1))
		.andExpect(jsonPath("$.name").value("TestAccount1"))
		.andExpect(jsonPath("$.balance").value(1000))
		.andExpect(jsonPath("$.containInGenBalance").value(true));
	}
	@Test
	@WithUserDetails("admin")
	void cashAccountInfo_WithAnotherDetails() throws Exception{
		mockMvc.perform(get("/cashAccounts/{id}", 1))
		.andExpect(status().isForbidden())
		.andExpect(jsonPath("$.Exception").value("Пользователь не обладает правами совершать это действие"));
	}
	@Test
	@WithUserDetails("user")
	void cashAccountInfo_NotExistsAccount() throws Exception{
		mockMvc.perform(get("/cashAccounts/{id}", 3))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.Exception").value("Счет с таким id не найден в базе данных"));
	}
	@Test
	void cashAccountInfo_WithoutDetails() throws Exception{
		mockMvc.perform(get("/cashAccounts/{id}", 1))
		.andExpect(status().isForbidden());
	}
	@Test
	@WithUserDetails("user")
	void addNewCashAccount() throws Exception{
		mockMvc.perform(post("/cashAccounts")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createdAccount)))
		.andExpect(status().isCreated());
		
		mockMvc.perform(get("/cashAccounts/{id}", 3))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id").value(3))
		.andExpect(jsonPath("$.name").value("NewAccount"))
		.andExpect(jsonPath("$.balance").value(1000))
		.andExpect(jsonPath("$.containInGenBalance").value(false));
	}
	@Test
	@WithUserDetails("admin")
	void addNewCashAccount_ExistsNameButAnotherUser() throws Exception{
		createdAccount.setName("TestAccount1");
		mockMvc.perform(post("/cashAccounts")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createdAccount)))
		.andExpect(status().isCreated());
		
		mockMvc.perform(get("/cashAccounts/{id}", 3))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id").value(3))
		.andExpect(jsonPath("$.name").value("TestAccount1"))
		.andExpect(jsonPath("$.balance").value(1000))
		.andExpect(jsonPath("$.containInGenBalance").value(false));
	}
	@Test
	void addNewCashAccount_WithoutDetails() throws Exception {
		mockMvc.perform(post("/cashAccounts")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createdAccount)))
		.andExpect(status().isForbidden());
	}
	
	@Test
	@WithUserDetails("user")
	void addNewCashAccount_FailNameValid() throws Exception {
		createdAccount.setName("TestAccount1");
		performPostWithFailValid("name", "Такой счет уже существует");
		
		for(String emptyName:new String[] {null,""}) {
			createdAccount.setName(emptyName);
			performPostWithFailValid("name","Поле не должно быть пустым");
		}
		
		createdAccount.setName("Very very very very large name size");
		performPostWithFailValid("name","Поле не должно превышать 20 символов");
	}
	@Test
	@WithUserDetails("user")
	void addNewCashAccount_FailBalanceValid() throws Exception {
		createdAccount.setBalance(null);
		performPostWithFailValid("balance","Поле не должно быть пустым");
	}
	void performPostWithFailValid(String field, String exceptionMessage) throws Exception{
		mockMvc.perform(post("/cashAccounts")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createdAccount)))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.Exception").value("Field - "+field+", error - "+exceptionMessage+";"));
	}
	
	@Test
	void editCashAccount_WithoutDetails() throws Exception {
		mockMvc.perform(patch("/cashAccounts")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(editedAccount)))
		.andExpect(status().isForbidden());
	}
	
	@Test
	@WithUserDetails("user")
	void editCashAccount() throws Exception{
		performPatch();
		editedAccount.setName("EditName");
		performPatch();
	}
	
	void performPatch() throws Exception{
		mockMvc.perform(patch("/cashAccounts")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(editedAccount)))
		.andExpect(status().isOk());
		
		mockMvc.perform(get("/cashAccounts/{id}", editedAccount.getId()))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id").value(editedAccount.getId()))
		.andExpect(jsonPath("$.name").value(editedAccount.getName()))
		.andExpect(jsonPath("$.balance").value((int)editedAccount.getBalance().doubleValue()))
		.andExpect(jsonPath("$.containInGenBalance").value(editedAccount.isContainInGenBalance()));	
	}
	
	@Test
	@WithUserDetails("user") 
	void removeCashAccount_UsersAccount() throws Exception {
		mockMvc.perform(delete("/cashAccounts/{id}",1))
		.andExpect(status().isNoContent());
		
		mockMvc.perform(get("/cashAccounts/{id}", 1))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.Exception").value("Счет с таким id не найден в базе данных"));
	}
	
	@Test
	@WithUserDetails("admin")
	void removeCashAccount_AnotherUsersAccount() throws Exception {
		mockMvc.perform(delete("/cashAccounts/{id}",1))
		.andExpect(status().isForbidden())
		.andExpect(jsonPath("$.Exception").value("Пользователь не обладает правами совершать это действие"));
	}
	@Test
	void removeCashAccount_WithoutDetails() throws Exception {
		mockMvc.perform(delete("/cashAccounts/{id}", 1))
		.andExpect(status().isForbidden());
	}

}
