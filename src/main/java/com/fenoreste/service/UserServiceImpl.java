package com.fenoreste.service;

import com.fenoreste.dao.UserRepository;
import com.fenoreste.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class UserServiceImpl implements IUserService,UserDetailsService{
    
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findUserByUsername(username);
		if(user == null) {
			throw new UsernameNotFoundException("Usuario no valido");
		}
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), Arrays.asList(new SimpleGrantedAuthority("ROLE ADMIN")));
	}

	@Override
	@Transactional(readOnly = true)
	public List<User> findAll() {
    	return (List<User>)userRepository.findAll();
	}

	@Override
	public void save(User user) {
		userRepository.save(user);
	}

	@Override
	@Transactional(readOnly = true)
	public User findById(Integer id) {
		
		return userRepository.findById(id).orElse(null);
	}

	

}
