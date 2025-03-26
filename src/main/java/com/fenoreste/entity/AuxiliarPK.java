package com.fenoreste.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuxiliarPK implements Serializable {
	
	private static final long serialVersionUID = 1L;
     
	 @Column(name = "idorigenp", nullable = false)
    private Integer idorigenp;
	
	 @Column(name = "idproducto",nullable = false)
    private Integer idproducto;
	
	@Column(name = "idauxiliar" , nullable = false)
    private Integer idauxiliar;

   
}
