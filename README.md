# RutaLimpia — Microservicio de Rutas

## 1. Descripción

El microservicio `rutas-service` forma parte del sistema RutaLimpia, una solución orientada a la gestión de solicitudes de retiro y planificación de rutas.

Este microservicio permite:

- Administrar hojas de ruta por camión y día.
- Registrar las paradas que recibe desde el proceso de asignación.
- Permitir que los conductores consulten sus hojas de ruta.
- Registrar si un retiro fue realizado o fallido.
- Comunicar el resultado del retiro a `solicitudes-service` para actualizar el estado de la solicitud.

## 2. Tecnologías utilizadas

- Java 25 (versión de compilación configurada).
- Spring Boot 4.1.1.
- Spring Web MVC.
- Spring Data JPA y Hibernate.
- Spring Security y JWT.
- MySQL 8.4.
- Docker Desktop.
- Apache Maven mediante Maven Wrapper.
- Swagger / OpenAPI para documentación de endpoints.

## 3. Requisitos previos

Antes de ejecutar el proyecto, se necesita:

- Git instalado.
- JDK 25 o una versión compatible.
- Docker Desktop instalado y funcionando.
- MySQL 8.4 disponible en el puerto 3306.
- PowerShell para ejecutar los comandos de este documento.

No es necesario instalar Maven por separado, porque el repositorio incluye Maven Wrapper.

Postman es opcional para probar los endpoints HTTP.

## 4. Clonar el repositorio

Abrir PowerShell y ejecutar:

```powershell
git clone https://github.com/Ruta-Limpia/rutas-service.git
cd rutas-service
```

## 5. Iniciar MySQL con Docker

El microservicio utiliza una base de datos MySQL llamada `rl_rutas`.

Para el entorno de desarrollo se utiliza un contenedor Docker llamado `mysql-rutalimpia-rutas`.

Si el contenedor ya existe, iniciarlo mediante:

```powershell
docker start mysql-rutalimpia-rutas
```

Comprobar que esté funcionando:

```powershell
docker ps
```

El contenedor debe aparecer con estado `Up` y tener el puerto `3306` publicado.

Si se está configurando el entorno por primera vez, se puede crear un contenedor de desarrollo con:

```powershell
docker run -d --name mysql-rutalimpia-rutas -e MYSQL_ROOT_PASSWORD=root -p 3306:3306 -v rutalimpia-rutas-data:/var/lib/mysql mysql:8.4
```

**Nota:** las credenciales `root/root` se utilizan únicamente como ejemplo de desarrollo local. No deben utilizarse en producción.

## 6. Configurar variables de entorno

El microservicio necesita variables de entorno para validar tokens JWT y autorizar las comunicaciones internas.

En PowerShell:

```powershell
$env:JWT_SECRET="REEMPLAZAR_POR_SECRETO_COMPARTIDO"
$env:INTERNAL_KEY="REEMPLAZAR_POR_CLAVE_INTERNA"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="root"
```

El valor de `JWT_SECRET` debe coincidir con el secreto utilizado por `auth-service`.

El valor de `INTERNAL_KEY` debe coincidir con la clave utilizada por los servicios que realizan llamadas internas a `rutas-service`.

Las variables configuradas de esta manera permanecen disponibles en la sesión actual de PowerShell. Si se abre una nueva terminal, será necesario configurarlas nuevamente.

## 7. Compilar y ejecutar las pruebas con Maven

Desde la carpeta principal del proyecto:

```powershell
.\mvnw.cmd clean package
```

Este comando realiza las siguientes tareas:

1. Limpia los archivos de compilaciones anteriores.
2. Compila el código fuente.
3. Compila y ejecuta las pruebas automatizadas.
4. Genera un archivo JAR ejecutable.

Una compilación correcta debe finalizar con:

```text
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

Actualmente el proyecto incluye una prueba básica `contextLoads()`, que comprueba que el contexto de Spring Boot puede iniciarse.

La ejecución de esta prueba requiere que estén disponibles las configuraciones necesarias y la base de datos MySQL.

## 8. Ejecutar el microservicio

Una vez compilado el proyecto, ejecutar:

```powershell
java -jar .\target\rutas-service-0.0.1-SNAPSHOT.jar
```

El microservicio utiliza el puerto `8084`.

Cuando inicia correctamente, la consola muestra mensajes similares a:

```text
Tomcat started on port 8084 (http)
Started RutasServiceApplication
```

Durante el inicio, Spring Boot establece la conexión con la base de datos MySQL `rl_rutas`.

Para detener el microservicio, presionar `Ctrl + C` en la terminal donde se está ejecutando.

**Importante:** detener la aplicación antes de volver a ejecutar `.\mvnw.cmd clean package`, ya que Windows puede impedir que Maven elimine un archivo JAR que continúa en uso.

## 9. Comprobar que el servicio responde

Con el microservicio ejecutándose, abrir otra terminal PowerShell y ejecutar:

```powershell
curl.exe -i http://localhost:8084/api/v1/rutas
```

Si no se proporciona un token JWT, el servicio debe responder:

```http
HTTP/1.1 401 Unauthorized
```

Esta respuesta indica que el servidor está disponible y que el endpoint requiere autenticación.

Para consultar las rutas correctamente, se necesita un token JWT válido con los permisos correspondientes.

## 10. Endpoints principales

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/v1/rutas` | Consultar rutas; requiere autorización de administrador. |
| GET | `/api/v1/rutas/mi-hoja?fecha=AAAA-MM-DD` | Consultar la hoja de ruta del conductor. |
| POST | `/api/v1/internal/rutas/paradas` | Registrar paradas mediante una llamada interna autorizada. |
| PATCH | `/api/v1/rutas/paradas/{id}/resultado` | Registrar el resultado de una parada. |

Los endpoints protegidos requieren un token JWT válido.

Los endpoints internos utilizan la cabecera `X-Internal-Key`.

## 11. Documentación de API

Si Swagger está habilitado en la aplicación, se puede acceder desde:

http://localhost:8084/swagger-ui.html

La documentación permite consultar las operaciones disponibles y los datos requeridos por cada endpoint.

## 12. Integración con otros microservicios

`rutas-service` forma parte de una arquitectura de microservicios que incluye:

- `auth-service`: autenticación y emisión de tokens JWT.
- `solicitudes-service`: administración de solicitudes de retiro.
- `flota-service`: administración de camiones.
- `rutas-service`: administración de hojas de ruta y resultados de paradas.

Para realizar pruebas completas de integración, los servicios involucrados deben encontrarse disponibles y compartir la configuración de seguridad correspondiente.

## 13. Observaciones

- La base de datos se configura mediante `application.properties` y variables de entorno.
- Las contraseñas y claves de ejemplo deben reemplazarse en entornos distintos al desarrollo local.
- La prueba `contextLoads()` verifica el arranque del contexto, pero no sustituye las pruebas funcionales de los endpoints.
- La ejecución local se ha comprobado mediante Maven, Docker y el archivo JAR generado.