DROP TABLE IF EXISTS digital_doc_legalario;
CREATE TABLE digital_doc_legalario(
    idorigenp integer,
    idproducto integer,
    idauxiliar integer,
    fecha_captura date,
    status varchar(45),
    tidentidades integer,
    ok_identidad boolean,
    iddocto_creado text,
    ok_docto_creado boolean,
	enviado_firmantes boolean,
    firmado boolean,
	mensajeFinal text
);


DROP TABLE IF EXISTS identidad_creada;
CREATE TABLE identidad_creada
(
    idorigenp    integer,
    idproducto   integer,
    idauxiliar   integer,
    ididentidad  text,
    fecha_creada timestamp with time zone,
    confirmada   boolean

);
