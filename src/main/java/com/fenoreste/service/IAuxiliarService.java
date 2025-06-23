package com.fenoreste.service;

import com.fenoreste.entity.Auxiliar;
import com.fenoreste.entity.AuxiliarPK;
import com.fenoreste.entity.PersonaPK;

public interface IAuxiliarService {

    public Auxiliar buscarPorId(AuxiliarPK auxiliarPK);
    public Auxiliar buscarPorOgsyProducto(PersonaPK pk,Integer idproducto);

}
