package ru.ochkasovap.homeAccountingRest.controllers;

import jakarta.validation.Valid;
import ru.ochkasovap.homeAccountingRest.dto.CategoryDTO;
import ru.ochkasovap.homeAccountingRest.models.Category;
import ru.ochkasovap.homeAccountingRest.security.UserDetailsImpl;
import ru.ochkasovap.homeAccountingRest.services.CategoriesService;
import ru.ochkasovap.homeAccountingRest.util.OperationType;
import ru.ochkasovap.homeAccountingRest.util.exceptions.CategoryNotValidException;
import ru.ochkasovap.homeAccountingRest.util.exceptions.HomeAccountingException;
import ru.ochkasovap.homeAccountingRest.util.validators.CategoryValidator;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categories")
public class CategoryController extends AbstractHomeAccountingController{

	private final CategoriesService service;
	private final CategoryValidator validator;
	private final ModelMapper modelMapper;

	@Autowired
	public CategoryController(CategoriesService service, CategoryValidator validator, ModelMapper modelMapper) {
		super();
		this.service = service;
		this.validator = validator;
		this.modelMapper = modelMapper;
	}

	@GetMapping("/{type}")
	public ResponseEntity<List<CategoryDTO>> showCategories(@AuthenticationPrincipal UserDetailsImpl userDetails,
			@PathVariable("type") OperationType type) {
		List<CategoryDTO> categories = service.findAllByUser(userDetails.getUser().getId(), type)
				.stream()
				.map(this::convertCategory)
				.toList();
		return new ResponseEntity<List<CategoryDTO>>(categories, HttpStatus.OK);
	}

	@GetMapping("/{type}/{id}")
	public ResponseEntity<CategoryDTO> showCategoryInfo(@PathVariable("id") int id,
			@PathVariable("type") OperationType type, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		try {
			CategoryDTO category = convertCategory(service.findById(userDetails.getUser().getId(), id, type.getCategoryClass()));
			return new ResponseEntity<CategoryDTO>(category, HttpStatus.OK);
		} catch (NullPointerException ex) {
			throw new HomeAccountingException("Категории с таким id не существует");
		}
	}

	@PostMapping("/{type}")
	public ResponseEntity<Void> addNewCategory(@AuthenticationPrincipal UserDetailsImpl userDetails,@PathVariable("type") OperationType type,
			@RequestBody @Valid CategoryDTO categoryDTO, BindingResult bindingResult) {
		Category category = convertDTO(categoryDTO, type);
		category.setUser(userDetails.getUser());
		validator.validate(category, bindingResult);
		if(bindingResult.hasErrors()) {
			throw new CategoryNotValidException(bindingResult);
		}
		service.create(userDetails.getUser().getId(), category);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PatchMapping("/{type}")
	public ResponseEntity<Void> editCategory(@RequestBody @Valid CategoryDTO categoryDTO, BindingResult bindingResult,
			@PathVariable("type") OperationType type, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		Category category = convertDTO(categoryDTO, type);
		category.setUser(userDetails.getUser());
		validator.validate(category, bindingResult);
		if(bindingResult.hasErrors()) {
			throw new CategoryNotValidException(bindingResult);
		}
		service.edit(category);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/{type}/{id}")
	public ResponseEntity<Void> deleteCategory(@PathVariable("id") int categoryID, @PathVariable("type") OperationType type,
			@AuthenticationPrincipal UserDetailsImpl userDetails) {
		service.remove(userDetails.getUser().getId(), categoryID, type);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	private CategoryDTO convertCategory(Category category) {
		return modelMapper.map(category, CategoryDTO.class);
	}

	private Category convertDTO(CategoryDTO categoryDTO, OperationType type) {
		Class<Category> categoryClass = type.getCategoryClass();
		return modelMapper.map(categoryDTO, categoryClass);
	}
}
