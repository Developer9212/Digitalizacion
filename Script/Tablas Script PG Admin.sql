
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



DELETE FROM tablas WHERE idtabla='cajero_receptor' AND idelemento='lista_negra';
INSERT INTO tablas(idtabla,idelemento,nombre,dato2)VALUES('cajero_receptor','lista_negra','Bloqueo de personas mediante sopar','atms_lista_negra');


select * from sopar limit 5


select idorigen,idgrupo,idsocio from auxiliares where idorigenp=30291 and idproducto=110 and idauxiliar=182
select idorigen,idgrupo,idsocio from auxiliares where idorigenp=30291 and idproducto=32664 and idauxiliar=347

30291 10 1295
30291 10 296878

select * from sopar limit 2;

update origenes set fechatrabajo='01/05/2025'

insert into sopar values(30291,10,1295,999,'atms_lista_negra')
insert into sopar values(30291,10,296878,999,'atms_lista_negra')

de