package com.fenoreste.dao;

import com.fenoreste.entity.Amortizacion;
import com.fenoreste.entity.AuxiliarPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import javax.persistence.OrderBy;

public interface AmortizacionDao extends JpaRepository<Amortizacion,Integer> {

	@Query(value = "SELECT * FROM amortizaciones a WHERE a.idorigenp=?1 AND a.idproducto=?2 AND a.idauxiliar=?3 ORDER BY vence ASC LIMIT 1",nativeQuery = true)
	public Amortizacion buscarPrimeraAmortizacion(Integer idorigenp,Integer idproducto,Integer idauxiliar);
	@Query(value = "SELECT * FROM amortizaciones a WHERE a.idorigenp=?1 AND a.idproducto=?2 AND a.idauxiliar=?3 ORDER BY vence DESC LIMIT 1",nativeQuery = true)
	public Amortizacion buscarUltimaAmortizacion(Integer idorigenp,Integer idproducto,Integer idauxiliar);
	
	@Query(value="SELECT * FROM  amortizaciones a WHERE a.idorigenp=?1 AND a.idproducto=?2 AND a.idauxiliar=?3 ORDER BY vence ASC",nativeQuery=true)
	public List<Amortizacion> todasPorOpa(Integer idorigenp,Integer idproducto,Integer idauxiliar);
}
