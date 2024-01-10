package ru.ochkasovap.homeAccountingRest.models;

import jakarta.persistence.*;
import java.util.List;


@Entity
@Table(name="roles")
public class Role {
	public static final String ADMIN = "ADMIN";
	public static final String USER = "USER";
	
	@Id
	private int id;
	private String name;

	@OneToMany(mappedBy="role")
	private List<User> users;

	public Role() {
	}
	
	public Role(Integer id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<User> getUsers() {
		return this.users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

}
