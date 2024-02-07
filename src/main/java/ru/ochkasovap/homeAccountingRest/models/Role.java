package ru.ochkasovap.homeAccountingRest.models;

import jakarta.persistence.*;
import java.util.List;


@Entity
@Table(name="roles")
public class Role extends AbstractModel {
	public static final String ADMIN = "ADMIN";
	public static final String USER = "USER";
	
	private String name;

	@OneToMany(mappedBy="role")
	private List<User> users;

	public Role() {
	}
	
	public Role(int id, String name) {
		super();
		this.setId(id);
		this.name = name;
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
