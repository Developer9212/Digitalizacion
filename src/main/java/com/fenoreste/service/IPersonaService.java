package com.fenoreste.service;

import com.fenoreste.entity.Persona;
import com.fenoreste.entity.PersonaPK;
import org.springframework.stereotype.Service;




public interface IPersonaService {
	
	public Persona buscarPorId(PersonaPK pk);



}
 