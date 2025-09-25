package com.fenoreste.service;

import com.fenoreste.dao.TablaDao;
import com.fenoreste.entity.Tabla;
import com.fenoreste.entity.TablaPK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TablaServiceImpl implements ITablaService{
    
	@Autowired
	private TablaDao tablaDao;
	
	@Override
	public Tabla buscarPorId(TablaPK pk) {
		return tablaDao.findById(pk).orElse(null);
	}

	@Override
	public void insertar(Tabla tabla) {
		tablaDao.save(tabla);
	}

	@Override
	public Tabla buscarPorIdProducto(Integer idProducto) {
		return tablaDao.TablaPorIdProducto(String.valueOf(idProducto));
	}

	@Override
	public Tabla buscarPorIdProductoArray(Integer idProducto) {
		return tablaDao.TablaPorIdProductoArray(String.valueOf(idProducto));
	}

}
