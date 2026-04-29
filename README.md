# OPTIMAK (Todos los modulos codificados en java)

Proyecto: OPTIMAK  
Evidencia: Módulos de software codificados y probados GA7-220501096-AA2-EV02  
Aprendiz: Adrián Arias Ríos  
Ficha: 3118499 - Análisis y Desarrollo de Software - SENA  
Instructor: Eduer Pabon Morales

## ¿Qué hacen estos módulos desarrollados?

Estos módulos implementan el desarrollo completo para todo el sistema OPTIMAK, cumpliendo con todos los requisitos, analisis y planeación estudiados para mi proyecto hasta ahora en el componente, conectado a una base de datos MySQL usando JDBC y conectado tambien al front.

## Tecnologías usadas (para esta tarea)

Java 17 
Jakarta Servlet 6 
JSP (JavaServer Pages) 
JDBC 
MySQL 8.0 CE 
Apache Maven 
Apache Tomcat 10.1


## Requisitos previos

Antes de correr el proyecto necesita tener instalado:

Java JDK 17
Maven 
Apache Tomcat 10.1
MySQL 8 y base de datos con los dos scripts aplicados, (el script principal obviamente y otro que puse en la carpeta con datos para pruebas de uso


## Paso 1 — Instalar Maven

Descarga Maven
Descarga el archivo ZIP que dice `apache-maven-3.x.x-bin.zip`
Descomprímelo en una carpeta `C:\Program Files\Apache\maven`
Agrega Maven al PATH de Windows
Verifica en CMD o PowerShell

## Paso 2 — Instalar Apache Tomcat 10.1

Tomcat es como el servidor 
Descarga Tomcat 10.1
> usa Tomcat 10.x, no Tomcat 9   
> El proyecto usa Jakarta Servlet 6 y ese solo funciona con Tomcat 10.


## Paso 3 — Preparar la base de datos
En mysql
Ejecuta el script principal `NE SCRIPT BD OPTIMAK CORREG 1.3.sql` que está en el repositorio, eso para crear la base de datos `optimak_db` con todas sus tablas.


Luego ejecutar el script que se llama `datos_prueba unificados.sql`




## Paso 4 — Modificar credenciales para que el back pueda acceder a mysql

Abra el archivo `\optimak-gestion-produccion\src\main\java\co\optimak\dao\ConexionBD.java` y revisa estas líneas:

private static final String USUARIO  = "root";
private static final String PASSWORD = "root";

si el Mysql local suyo de su PC tiene otro usuario o contraseña cambiela ahí



## Paso 5 — Compilar el proyecto

1. Abre PowerShell o CMD como administrador mejor

2. Navegar hasta la carpeta del proyecto o:

Escribes cd / y escribe la ruta de la carpeta

3. Compila y genera el archivo WAR con el comando:

mvn clean package

4. Si todo va bien aparece el mensaje en verde BUILD SUCCESS

Dentro de la carpeta se genera una carpeta llamada target y adentro se genera el archivo optimak.war


## Paso 6 — Desplegar en Tomcat

Copia el archivo optimak.war dentro de la carpeta webapps de Tomcat.  
si sale bien , a los segundos aparece una carpeta nueva ahi dentro de webapps que se llama optimak


Si uno tiene instalado Tomcat como Servicio, ya debería estar corriendo
sino en la terminal que tiene abierta escribe: startup.bat  // o en una nueva: C:\tomcat\bin\startup.bat
(Siempre y cuando esté configurado en el path) (si no lo tiene configurado, vaya a la carpeta bin de Tomcat y doble clic en startup.bat)
En ese punto se abre una ventana negra que debe quedarse abierta. es como un servidor en el mismo computador , si se cierra es como si apagara el servidor




Abrir el navegador y ve a:    http://localhost:8080/optimak/login

Deberías ver la primera pagina que el del inicio de sesión

## Paso 7 — Iniciar sesión 

Puedes explorar cualquiera de los dos roles, si si ejecutaste los scripts estos datos de usuario te van a dejar ingresar correctamente:

>ROL ADMINISTRADOR:
Usuario: 1
Contraseña: admin123

>ROL OPERARIO:
Usuario: 1000000001
Contraseña: operario123

Explora los dos roles con los datos de ejemplo ejecutados en el script o tambien puedes crear datos de campo ya desde la pagina, desde el despliegue local. 


