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
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.minidev.json.JSONArray;
import net.minidev.json.parser.JSONParser;
import ru.ochkasovap.homeAccountingRest.dto.OperationDTO;
import ru.ochkasovap.homeAccountingRest.util.ExceptionMessageBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Sql (value = {"/CreateTableScript.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
class OperationsControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	
	private OperationDTO createdOperation;
	private OperationDTO editedOperation;
	private String[] types;
	
	@BeforeEach
	void setUp() {
		createdOperation = new OperationDTO.Builder()
				.amount(new BigDecimal(100))
				.cashAccount("TestAccount1")
				.category("TestCategory1")
				.comment("Comment")
				.date(new GregorianCalendar(2023, 0, 2).getTime())
				.build();
		editedOperation = new OperationDTO.Builder()
				.id(1)
				.amount(new BigDecimal(200))
				.cashAccount("TestAccount2")
				.category("TestCategory2")
				.comment("NewComment")
				.date(new GregorianCalendar(2023, 0, 3).getTime())
				.build();
		types = new String[] {"income","outcome"};
	}
	@Test
	void showOperationsList_WithoutDetails() throws Exception {
		for(String type:types) {
			mockMvc.perform(get("/operations/{type}", type))
			.andExpect(status().isForbidden());
		}
	}
	
	@Test
	@WithUserDetails("user")
	void showOperationsList() throws Exception {
		String requestBody = objectMapper.writeValueAsString(List.of(
				Map.of("id",2,"amount",100.0, "account", "TestAccount1", "category", "TestCategory1","comment","TestComment","date","2022-12-31"),
				Map.of("id",1,"amount",100.0, "account", "TestAccount1", "category", "TestCategory1","comment","TestComment","date","2023-01-01")
				));
		JSONArray jsonArray = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(requestBody, JSONArray.class);
		for(String type:types) {
			mockMvc.perform(get("/operations/{type}", type)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(Map.of("filter",new OperationDTO()))))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", Matchers.containsInAnyOrder(jsonArray.toArray())));
		}
	}
	
	@Test
	void showOperation_WithoutDetails() throws Exception {
		for(String type:types) {
			mockMvc.perform(get("/operations/{type}/{id}", type, 1))
			.andExpect(status().isForbidden());
		}
	}
	
	@Test
	@WithUserDetails("user")
	void showOperation_UserDetails() throws Exception {
		for(String type:types) {
			mockMvc.perform(get("/operations/{type}/{id}", type, 1))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1))
			.andExpect(jsonPath("$.amount").value(100))
			.andExpect(jsonPath("$.account").value("TestAccount1"))
			.andExpect(jsonPath("$.category").value("TestCategory1"))
			.andExpect(jsonPath("$.comment", Matchers.is("TestComment")))
			.andExpect(jsonPath("$.date", Matchers.is("2023-01-01")));
		}
	}
	
	@Test
	@WithUserDetails("admin")
	void showOperation_AnotherUserDetails() throws Exception {
		showOperation_AnotherUserDetails("income");
		showOperation_AnotherUserDetails("outcome");
	}
	
	void showOperation_AnotherUserDetails(String type) throws Exception {
		mockMvc.perform(get("/operations/{type}/{id}", type, 1))
		.andExpect(status().isForbidden())
		.andExpect(jsonPath("$.Exception").value("Пользователь не обладает правами совершать это действие"));
	}
	
	@Test
	@WithUserDetails("user")
	void showOperation_NotExistsOperation() throws Exception {
		for(String type:types) {
			mockMvc.perform(get("/operations/{type}/{id}", type, 3))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.Exception").value("Операции с таким id не существует в базе данных"));
		}
	}
	
	@Test
	void createOperaion_withoutDetails() throws Exception {
		for (String type:types) {
			mockMvc.perform(post("/operations/{type}", type)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(createdOperation)))
			.andExpect(status().isForbidden());
		}
	}
	
	@Test
	@WithUserDetails("user")
	void createOperation() throws Exception{
		createOperation("income", 1100);
		createOperation("outcome", 1000);
	}
	
	void createOperation(String type, int expectedAmount) throws Exception {
		mockMvc.perform(post("/operations/{type}", type)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createdOperation)))
		.andExpect(status().isCreated());
		
		mockMvc.perform(get("/operations/{type}/{id}", type, 3))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id").value(3))
		.andExpect(jsonPath("$.amount").value(100))
		.andExpect(jsonPath("$.account").value("TestAccount1"))
		.andExpect(jsonPath("$.category").value("TestCategory1"))
		.andExpect(jsonPath("$.comment", Matchers.is("Comment")))
		.andExpect(jsonPath("$.date", Matchers.is("2023-01-01")));
		
		mockMvc.perform(get("/cashAccounts/{id}", 1))
		.andExpect(jsonPath("$.balance").value(expectedAmount));
	}
	
	@Test
	@WithUserDetails("user")
	void createOperation_FailValid() throws Exception{
		createdOperation.setComment("Very very very very very very very very very very long comment");
		for(String type:types) {
			createOperation_FailValid(type,Map.of("comment","Комментарий не должен превышать 50 символов"));
		}
		createdOperation.setComment("");
		createdOperation.setDate(new Date(System.currentTimeMillis()+1000*60*60*24));
		for(String type:types) {
			createOperation_FailValid(type, Map.of("date","Дата не может быть позже текущей"));
		}
	}
	@Test
	@WithUserDetails("user")
	void createOperation_NullFieldsValid() throws Exception {
		String emptyFieldMessage = "Поле не должно быть пустым";
		for(String type:types) {
			for(String field: new String[] {"account","date","category","amount"}) {
				setNull(field);
				createOperation_FailValid(type, Map.of(field, emptyFieldMessage));
				setUp();
			}
		}
	}
	private void setNull(String field) throws Exception {
		 StringBuilder name = new StringBuilder("set").append(field.substring(0,1).toUpperCase()).append(field.substring(1));
		 Class<OperationDTO> operationClass = OperationDTO.class;
		 Class<?> parameterType = OperationDTO.class.getDeclaredField(field).getType();
		 operationClass.getDeclaredMethod(name.toString(), parameterType).invoke(createdOperation,new Object[] { null });
	}
	void createOperation_FailValid(String type, Map<String, String> fieldsErrors) throws Exception{
		mockMvc.perform(post("/operations/{type}", type)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createdOperation)))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.Exception").value(ExceptionMessageBuilder.build(fieldsErrors)));
	}
	
	@Test
	void editOperation_WithoutUserDetails() throws Exception{
		for(String type:types) {
			mockMvc.perform(patch("/operations/{type}", type)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(editedOperation)))
			.andExpect(status().isForbidden());
		}
	}
	
	@Test
	@WithUserDetails("user")
	void editOperation() throws Exception{
		editOperation("income", Map.of("Account1Balance", 900, "Account2Balance", 1200));
		editOperation("outcome", Map.of("Account1Balance", 1000, "Account2Balance", 1000));
	}
	
	void editOperation(String type, Map<String, Object> jsonExpected) throws Exception{
		mockMvc.perform(patch("/operations/{type}", type)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(editedOperation)))
		.andExpect(status().isOk());
		
		mockMvc.perform(get("/operations/{type}/{id}", type, editedOperation.getId()))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id").value(editedOperation.getId()))
		.andExpect(jsonPath("$.amount").value((int)editedOperation.getAmount().doubleValue()))
		.andExpect(jsonPath("$.account").value(editedOperation.getAccount()))
		.andExpect(jsonPath("$.category").value(editedOperation.getCategory()))
		.andExpect(jsonPath("$.comment", Matchers.is(editedOperation.getComment())))
		.andExpect(jsonPath("$.date", Matchers.is("2023-01-02")));
		
		mockMvc.perform(get("/cashAccounts/{id}", 1))
		.andExpect(jsonPath("$.balance").value(jsonExpected.get("Account1Balance")));
		
		mockMvc.perform(get("/cashAccounts/{id}", 2))
		.andExpect(jsonPath("$.balance").value(jsonExpected.get("Account2Balance")));
	}
	
	@Test
	@WithUserDetails("admin")
	void editOperation_AnotherUserDetails() throws Exception{
		for(String type:types) {
			mockMvc.perform(patch("/operations/{type}", type)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(editedOperation)))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.Exception").value("Пользователь не обладает правами совершать это действие"));
		}
	}
	
	@Test
	void deleteOperation_WithoutDetails() throws Exception{
		for(String type:types) {
			mockMvc.perform(delete("/operations/{type}/{id}", type, 1))
			.andExpect(status().isForbidden());
		}
	}
	
	@Test
	@WithUserDetails("user")
	void deleteOperation() throws Exception{
		for(String type:types) {
			mockMvc.perform(delete("/operations/{type}/{id}", type, 1))
			.andExpect(status().isNoContent());
			
			mockMvc.perform(get("/operations/{type}/{id}", type, 1))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.Exception").value("Операции с таким id не существует в базе данных"));
		}
	}
	
	@Test
	@WithUserDetails("admin")
	void deleteOperation_AnotherUserDetails() throws Exception{
		for(String type:types) {
			mockMvc.perform(delete("/operations/{type}/{id}", type, 1))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.Exception").value("Пользователь не обладает правами совершать это действие"));
		}
	}

}
