package com.fenoreste.dao;

import com.fenoreste.entity.Persona;
import com.fenoreste.entity.PersonaPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface PersonaDao extends JpaRepository<Persona, PersonaPK> {
	
	@Query(value = "SELECT * FROM personas WHERE curp=?1 AND idgrupo=?2",nativeQuery = true)
	public Persona findByCurpIdgrupo(String curp,Integer idgrupo);
      
}
