
DELETE FROM tablas WHERE idtabla='digitalizacion' AND idelemento='login_credentials';
INSERT INTO tablas(idtabla,idelemento,dato1,dato2) VALUES('digitalizacion','login_crendentials','HcJV6W85L8d9','ctello@csn.coop');


DROP TABLE IF EXISTS digital_doc_legalario;
CREATE TABLE digital_doc_legalario(
    idorigenp integer,
    idproducto integer,
    idauxiliar integer,
    fecha_captura date,
    status varchar(45),
    ididentidad text,
    ok_identidad boolean,
    iddocto_creado text,
    ok_docto_creado boolean,
	enviado_firmantes boolean,
    firmado boolean,
	mensajeFinal text
);