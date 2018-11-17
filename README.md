# SCRIPTS PSQL para importar la DB de StackOverflow
El repositorio contiene scripts en PosgreSQL para importar
archivos en formato XML pertenecientes a la base de datos de 
StackOverflow. Se recomienda adaptarlos a conveniencia.

# Fase Inicial
En la carpeta import se encuentra una serie de scripts que permiten
importar los archivos XML mediante un programa en Java.
Es importante resaltar que dependiendo de la versi�n de PostgreSQL
se requerir�n ciertos cambios. Se recomienda utilizar una versi�n
anterior a la v9.2. Debido al cambio de comportamiento en la funci�n xpath().

En el archivo proc_firma.sql se encuentra un resumen de los pasos a seguir.
As� como la separaci�n de las preguntas y respuestas que se utilizaran
posteriormente.

# Fase MinHash
En la carpeta principal se encuentra un proyecto de Eclipse CDT que permite
generar el contenido de las firmas. Para emplearlo se tiene que configurar
adecuadamente la conexi�n con la base de datos y haber ejecutado con anterioridad
el script import_code.sql.

# Fase Locality-Sensitive Hashing
Posteriormente, se pueden generar las claves mediante el script create_lsh.sql,
get_lsh.sql permite generar el procedimiento para obtener los candidatos.

