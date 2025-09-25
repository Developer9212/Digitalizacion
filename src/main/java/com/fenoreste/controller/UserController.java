package com.fenoreste.controller;


import com.fenoreste.entity.User;
import com.fenoreste.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping({"/api" })
public class UserController {
    
	@Autowired
	private IUserService userSevice;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@GetMapping("/users")
	public ResponseEntity<?>obtnerUsuarios(){
		List<User>users = userSevice.findAll();
		if(users != null) {
			return new ResponseEntity<>(users,HttpStatus.OK);
		}else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@PostMapping("/create_user")
	public ResponseEntity<?>crearUsuario(@RequestBody User user){
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setId(user.getId());
		user.setCreate_at(new Date());
		this.userSevice.save(user);
		return new ResponseEntity<>(HttpStatus.CREATED);
		
	}
	
}
