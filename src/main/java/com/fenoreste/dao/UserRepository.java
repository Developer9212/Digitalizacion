package com.fenoreste.dao;

import com.fenoreste.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {

	public User findUserByUsername(String username);
	
}
