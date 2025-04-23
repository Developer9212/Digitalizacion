package com.fenoreste.service;

import com.fenoreste.entity.User;

import java.util.List;

public interface IUserService {
   
	public List<User>findAll();
	
	public void save(User user);
	
	public User findById(Integer id);
}
