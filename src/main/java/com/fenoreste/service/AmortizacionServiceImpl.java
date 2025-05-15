package com.fenoreste.service;

import com.fenoreste.dao.AmortizacionDao;
import com.fenoreste.entity.Amortizacion;
import com.fenoreste.entity.AuxiliarPK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class AmortizacionServiceImpl implements IAmortizacionService{
    
	@Autowired
	private AmortizacionDao amortizacionDao;
	
	@Override
	public Amortizacion buscarPrimerPago(AuxiliarPK pk) {
		return amortizacionDao.buscarPrimeraAmortizacion(pk.getIdorigenp(),pk.getIdproducto(),pk.getIdauxiliar());
	}

	

	@Override
	public Amortizacion buscarUltimoPago(AuxiliarPK pk) {
		return amortizacionDao.buscarUltimaAmortizacion(pk.getIdorigenp(),pk.getIdproducto(),pk.getIdauxiliar());
	}

	@Override
	public List<Amortizacion> buscarTodasPorId(AuxiliarPK pk) {
		return amortizacionDao.todasPorOpa(pk.getIdorigenp(), pk.getIdproducto(),pk.getIdauxiliar());
	}

}
