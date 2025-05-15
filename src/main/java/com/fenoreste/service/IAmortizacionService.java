package com.fenoreste.service;

import com.fenoreste.entity.Amortizacion;
import com.fenoreste.entity.AuxiliarPK;

import java.util.List;

public interface IAmortizacionService {

	public Amortizacion buscarPrimerPago(AuxiliarPK pk);
	public Amortizacion buscarUltimoPago(AuxiliarPK pk);
	public List<Amortizacion> buscarTodasPorId(AuxiliarPK pk);
}
