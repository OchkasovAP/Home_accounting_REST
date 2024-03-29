package ru.ochkasovap.homeAccountingRest.controllers;

import jakarta.validation.Valid;
import ru.ochkasovap.homeAccountingRest.dto.EditUserDTO;
import ru.ochkasovap.homeAccountingRest.dto.RegistrationDTO;
import ru.ochkasovap.homeAccountingRest.dto.UserDTO;
import ru.ochkasovap.homeAccountingRest.models.Role;
import ru.ochkasovap.homeAccountingRest.models.User;
import ru.ochkasovap.homeAccountingRest.security.JWTUtil;
import ru.ochkasovap.homeAccountingRest.security.UserDetailsImpl;
import ru.ochkasovap.homeAccountingRest.services.UserService;
import ru.ochkasovap.homeAccountingRest.util.exceptions.HomeAccountingException;
import ru.ochkasovap.homeAccountingRest.util.exceptions.UserCannotBeRemovedException;
import ru.ochkasovap.homeAccountingRest.util.exceptions.UserNotValidException;
import ru.ochkasovap.homeAccountingRest.util.validators.UserCreationValidator;
import ru.ochkasovap.homeAccountingRest.util.validators.UserEditionValidator;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
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
@RequestMapping("/users")
public class UserController extends AbstractHomeAccountingController {

	private final UserService userService;
	private final UserEditionValidator editionValidator;
	private final UserCreationValidator creationValidator;
	private final ModelMapper modelMapper;
	private final JWTUtil jwtUtil;
	private final AuthenticationManager authenticationManager;

	@Autowired
	public UserController(UserService userService, UserEditionValidator userEditionValidator, ModelMapper modelMapper,
			JWTUtil jwtUtil, AuthenticationManager authenticationManager, UserCreationValidator creationValidator) {
		super();
		this.userService = userService;
		this.editionValidator = userEditionValidator;
		this.creationValidator = creationValidator;
		this.modelMapper = modelMapper;
		this.jwtUtil = jwtUtil;
		this.authenticationManager = authenticationManager;
	}

	@GetMapping()
	public List<UserDTO> showUsers() {
		return userService.findAll().stream().map(this::convertUser).toList();
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserDTO> userInfo(@PathVariable("id") int id,
			@AuthenticationPrincipal UserDetailsImpl userDetails) {
		if (userDetails.getUser().isAdmin() || userDetails.getUser().getId() == id) {
			try {
				return new ResponseEntity<>(convertUser(userService.findById(id)), HttpStatus.OK);
			} catch (NoSuchElementException ex) {
				throw new HomeAccountingException("Пользователь с таким id не существует");
			}
		}
		return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}

	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> autorization(@RequestBody RegistrationDTO userDTO) {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				userDTO.getLogin(), userDTO.getPassword());
		try {
			authenticationManager.authenticate(authenticationToken);
		} catch (AuthenticationException e) {
			return new ResponseEntity<Map<String, String>>(Map.of("message", "Некорректный логин или пароль"),
					HttpStatus.BAD_REQUEST);
		}
		String token = jwtUtil.generateToken(userDTO.getLogin());
		return new ResponseEntity<>(Map.of("jwt_token", token), HttpStatus.OK);
	}

	@PostMapping("/registration")
	public ResponseEntity<Map<String, String>> create(@RequestBody @Valid RegistrationDTO userDTO,
			BindingResult bindingResult) {
		creationValidator.validate(userDTO, bindingResult);
		if (bindingResult.hasErrors()) {
			throw new UserNotValidException(bindingResult);
		}
		User user = convertRegistrationDTO(userDTO);
		userService.create(user);
		String token = jwtUtil.generateToken(user.getLogin());
		return new ResponseEntity<>(Map.of("jwt_token", token), HttpStatus.CREATED);
	}

	@PatchMapping()
	public ResponseEntity<Void> edit(@AuthenticationPrincipal UserDetailsImpl userDetails,
			@RequestBody @Valid EditUserDTO userDTO, BindingResult bindingResult) {
		User currentUser = userDetails.getUser();
		if (currentUser.isAdmin() || userDetails.getUser().getId() == userDTO.getId()) {
			editionValidator.validate(userDTO, bindingResult);
			if (hasEditErrors(userDTO, bindingResult)) {
				throw new UserNotValidException(bindingResult);
			}
			if(currentUser.getId()==userDTO.getId()) {
				userDTO.setRole(currentUser.getRole().getName());
			}
			User user = convertEditDTO(userDTO);
			if (userDTO.getNewPassword() != null) {
				user.setPassword(userDTO.getNewPassword());
			}
			userService.edit(user);
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") int id,
			@AuthenticationPrincipal UserDetailsImpl userDetails) {
		User authenticateUser = userDetails.getUser();
		if (authenticateUser.getId() == id && authenticateUser.isAdmin()) {
			throw new UserCannotBeRemovedException("Администратор не может удалить самого себя");
		} else if (!authenticateUser.isAdmin() && authenticateUser.getId() != id) {
			throw new UserCannotBeRemovedException(
					"Пользователь в правами 'User' не может удалять других пользователей");
		}
		userService.delete(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	private boolean hasEditErrors(EditUserDTO authDTO, BindingResult bindingResult) {
		return bindingResult.hasErrors()&&(bindingResult.getFieldErrors().stream().anyMatch(t -> t.getField().equals("login"))
				||(Stream.of(authDTO.getNewPassword(),authDTO.getPassword(), authDTO.getRepeatedNewPassword()).anyMatch(t -> t!=null&&!t.isBlank())));
	}

	private User convertRegistrationDTO(RegistrationDTO userDTO) {
		User user = modelMapper.map(userDTO, User.class);
		return user;
	}
	private User convertEditDTO(EditUserDTO userDTO) {
		User user = modelMapper.map(userDTO, User.class);
		user.setRole(new Role(0, userDTO.getRole()));
		return user;
	}

	private UserDTO convertUser(User user) {
		UserDTO userDTO = modelMapper.map(user, UserDTO.class);
		userDTO.setRole(user.getRole().getName());
		return userDTO;
	}
}
