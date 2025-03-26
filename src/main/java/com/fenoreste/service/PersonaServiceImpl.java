package com.fenoreste.service;


import com.fenoreste.dao.PersonaDao;
import com.fenoreste.entity.Persona;
import com.fenoreste.entity.PersonaPK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonaServiceImpl implements IPersonaService{
	
	@Autowired
	private PersonaDao personaRepository;

	@Override
	public Persona buscarPorId(PersonaPK pk) {
		return personaRepository.getById(pk);
	}


	
	
	
	
	

}
