package com.fenoreste.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Random;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Data
public class User implements Serializable{
  
	@Id
	private Integer id;	
	private String username;
	private String password;
	@Temporal(TemporalType.DATE)
	private Date create_at;
	
	private static final long serialVersionUID = 1L;
	
}
