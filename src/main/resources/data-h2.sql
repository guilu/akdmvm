insert into BDD_AKDMVM.TB00_USER(first_name,last_name,email,password,enabled,locked) VALUES ('Diego','B H','user@gmail.com','$2a$10$.dNJIVmCUUqNBi8dTB4Tf.3teRGuZDpwGM2wcCeVwC9AC3SkNmKdu',true,false);
insert into BDD_AKDMVM.TB00_USER(first_name,last_name,email,password,enabled,locked) VALUES ('Diego','B H','admin@gmail.com','$2a$10$.dNJIVmCUUqNBi8dTB4Tf.3teRGuZDpwGM2wcCeVwC9AC3SkNmKdu',true,false);

insert into BDD_AKDMVM.TB00_ROLE(NAME) VALUES ('ROLE_USER');
insert into BDD_AKDMVM.TB00_ROLE(NAME) VALUES ('ROLE_ADMIN');
insert into BDD_AKDMVM.TB00_ROLE(NAME) VALUES ('ROLE_EDITOR');

insert into BDD_AKDMVM.TB00_PRIVILEGE(NAME) VALUES ('READ_PRIVILEGE');
insert into BDD_AKDMVM.TB00_PRIVILEGE(NAME) VALUES ('WRITE_PRIVILEGE');
insert into BDD_AKDMVM.TB00_PRIVILEGE(NAME) VALUES ('BLOG_PRIVILEGE');

INSERT INTO BDD_AKDMVM.TB00_ROLE_PRIVILEGE VALUES (1,1); -- USER

INSERT INTO BDD_AKDMVM.TB00_ROLE_PRIVILEGE VALUES (2,1); -- ADMIN
INSERT INTO BDD_AKDMVM.TB00_ROLE_PRIVILEGE VALUES (2,2);
INSERT INTO BDD_AKDMVM.TB00_ROLE_PRIVILEGE VALUES (2,3);

INSERT INTO BDD_AKDMVM.TB00_ROLE_PRIVILEGE VALUES (3,1); -- EDITOR
INSERT INTO BDD_AKDMVM.TB00_ROLE_PRIVILEGE VALUES (3,3);

INSERT INTO BDD_AKDMVM.TB00_USER_ROLE VALUES (1,1);
INSERT INTO BDD_AKDMVM.TB00_USER_ROLE VALUES (2,2); 


INSERT INTO BDD_AKDMVM.tb01_agrupacion VALUES (1,'CONOCIMIENTOS GENERALES'),
(2,'CONOCIMIENTOS ESPECIFICOS'),
(3,'TEMAS ESPECÍFICOS DEL SPEIS');

INSERT INTO BDD_AKDMVM.tb02_tema  VALUES (1,'Constitución Española',1),
(2,'Estatuto de Autonomía de la CV',1),
(3,'Ley de Bases del Régimen Local',1),
(4,'Ley de Régimen Local de la CV',1),
(5,'Ley de Prevención de Riesgos Laborales',1),
(6,'Estatuto Básico del Empleado Público',1),
(7,'Ordenación y Gestión de la FPV',1),
(8,'Ley de Incompatibilidades',1),
(9,'Derecho Administrativo',1),
(10,'Hacienda Pública y Administración Tributaria',1);


INSERT INTO BDD_AKDMVM.tb03_pregunta(ID,TEXTO,TEMA_ID,EXAMEN_ID) VALUES (1,'¿Qué Artículo de la Constitución está dedicado a la irretroactividad de las disposiciones  desfavorables o restrictivas de derechos individuales?',1,NULL),
(2,'Según el Artículo 1.2 de la Constitución Española, ¿De dónde emanan los poderes del Estado?',1,NULL),
(3,'¿Cómo se denomina el Título Preliminar de la C.E?',1,NULL),
(4,'¿Por qué no están constituidas las fuerzas armadas?',1,NULL),
(5,'Las bases de la organización militar se regulará conforme a los principios previstos en:',1,NULL),
(6,'Según la Constitución Española de 1978, las banderas y enseñas propias de las Comunidades Autónomas: (Comunidad de Madrid, 2014)',1,NULL),
(7,'La solidaridad entre las autonomías de las nacionalidades  y  regiones  que integran  la  nación española es  un derecho  que:',1,NULL),
(8,'Según el Artículo 1 de la Constitución Española,  la  forma  política del  Estado  español es: (Comunidad  de Madrid,  2014)',1,NULL),
(9,'Según la Constitución;  ¿Que  se  fundamenta en  la  indisoluble unidad de  la nación  española?',1,NULL),
(10,'¿Quién está sujeto a la Constitución y al resto del ordenamiento jurídico?',1,NULL);


INSERT INTO BDD_AKDMVM.tb04_respuesta(ID,CORRECTA,TEXTO,PREGUNTA_ID)  VALUES (1,false,'Artículo 9.2',1),
(2,true,'Artículo 9.3',1),
(3,false,'Artículo 9.4',1),
(4,false,'Artículo 10',1),
(5,false,'De la soberanía española',2),
(6,false,'De los poderes Públicos',2),
(7,false,'De la monarquía parlamentaria',2),
(8,true,'Del Pueblo Español',2),
(9,false,'De los principios básicos',3),
(10,false,'De los principios generales',3),
(11,false,'Preámbulo',3),
(12,true,'Ninguna es correcta',3),
(13,false,'La armada',4),
(14,false,'Ejercito de aire',4),
(15,false,'Ejército de tierra',4),
(16,true,'Ejercito de mar',4),
(17,false,'La Ley',5),
(18,false,'Una Ley Ordinaria',5),
(19,false,'Una Ley Orgánica',5),
(20,true,'La Constitución',5),
(21,false,'Representan un patrimonio cultural que será objeto de especial respeto y protección',6),
(22,true,'Podrán reconocerse en los estatutos',6),
(23,false,'Podrán reconocerse por los órganos autonómicos',6),
(24,false,'Deberán reconocerse en los respectivos estatutos',6),
(25,false,'Se protege',7),
(26,false,'Se garantiza',7),
(27,true,'Se reconoce y se garantiza',7),
(28,false,'Se reconoce',7),
(29,false,'La Democracia parlamentaria',8),
(30,true,'La Monarquía parlamentaria',8),
(31,false,'La Monarquía constitucional',8),
(32,false,'La Monarquía democrática',8),
(33,false,'España',9),
(34,true,'La Constitución',9),
(35,false,'El Estado',9),
(36,false,'El Estado español',9),
(37,false,'Los ciudadanos y la administración',10),
(38,true,'Los ciudadanos y los poderes públicos',10),
(39,false,'Las personas y la administración',10),
(40,false,'Los españoles y los poderes públicos',10);