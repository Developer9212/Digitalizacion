

DELETE FROM tablas WHERE idtabla='digitalizacion' AND idelemento='login_credentials';
INSERT INTO tablas(idtabla,idelemento,dato1,dato2) VALUES('digitalizacion','login_crendentials','HcJV6W85L8d9','ctello@csn.coop');

DELETE FROM tablas WHERE idtabla='digitalizacion' AND idelemento ='signer_data';
INSERT INTO tablas(idtabla,idelemento,dato2,dato3) VALUES('digitalizacion','signer_data','5bd09a7c08228b112371bdc3','67bf9b6e29787732747805e2');

DELETE FROM tablas WHERE idtabla='digitalizacion' AND idelemento ='token';
INSERT INTO tablas(idtabla,idelemento,dato2,dato3)VALUES('digitalizacion','token','fasasfasfsafasfsfasf','01/02/1970');


DELETE FROM TABLAS WHERE idtabla='digitalizacion' AND idelemento='user-ws';
INSERT INTO TABLAS(idtabla,idelemento,nombre,dato1,dato2) VALUES('digitalizacion','user-ws','User for api autenticate','test','test');


DELETE FROM TABLAS WHERE idtabla='digitalizacion' AND idelemento ='contrato_aniversario';
INSERT INTO TABLAS(idtabla,idelemento,dato1,dato2)VALUES('digitalizacion','contrato_aniversario','32644','67eadea8c046bd05d01058b2');

DELETE FROM TABLAS WHERE idtabla='digitalizacion' AND idelemento='maximo_en_solicitud';
INSERT INTO TABLAS(idtabla,idelemento,dato1)VALUES('digitalizacion','maximo_en_solicitud','50000');


DELETE FROM TABLAS WHERE idtabla='digitalizacion' AND idelemento='capital_en_riesgo';
INSERT INTO TABLAS(idtabla,idelemento,dato1,dato2)VALUES('digitalizacion','capital_en_riesgo','110','50000');
