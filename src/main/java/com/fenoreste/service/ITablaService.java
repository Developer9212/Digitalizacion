package com.fenoreste.service;

import com.fenoreste.entity.Tabla;
import com.fenoreste.entity.TablaPK;

public interface ITablaService {

	public Tabla buscarPorId(TablaPK pk);

	public void insertar(Tabla tabla);
}
