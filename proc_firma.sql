-- Arreglar indices para acelerar las busquedas.
CREATE INDEX idx_posttags ON posttags USING GIN (post, tag) WITH (fastupdate = OFF);
CREATE UNIQUE INDEX idx_tags_id ON tags (id) WITH (fillfactor = 100);
CREATE UNIQUE INDEX idx_tags_tagname ON tags (tagname) WITH (fillfactor = 100);

-- Para separar las Preguntas
DROP TABLE IF EXISTS questions;
CREATE TABLE questions(Id INTEGER);
INSERT INTO questions(Id) SELECT DISTINCT post FROM PostTags WHERE tag=8 OR tag=10; -- C or C++
ALTER TABLE questions ADD PRIMARY KEY (Id);

-- Para separar las respuestas
DROP TABLE IF EXISTS answers;
CREATE TABLE answers(Id INTEGER);
INSERT INTO answers(Id) SELECT id FROM posts p WHERE EXISTS (SELECT 1 FROM questions WHERE id = p.parentid);
ALTER TABLE answers ADD PRIMARY KEY (Id);

-- Total Posts C/C++.
--SELECT count(*) FROM questions;
-- 442450
--SELECT count(*) FROM answers;
-- 978214

-- Ejecutar import_code() para crear la tabla Codes.

-- Indice de la relación entre los post y los códigos
CREATE UNIQUE INDEX idx_postcodes_code ON postcodes (code) WITH (fillfactor = 100);
CREATE INDEX idx_postcodes_post ON postcodes (post) WITH (fillfactor = 100);

-- Total de Códigos.
SELECT count(*) FROM Codes;
-- 1377716

-- Ejecutar el indexador de Java para crear las firmas.

-- Ejecutar create_lsh.sql para crear el indice de busqueda.
