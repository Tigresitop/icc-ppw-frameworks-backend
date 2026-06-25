# Práctica 1: Configuración Inicial y Primer Endpoint en Spring Boot

**Autor:** John Tigre  
**Materia:** Programación y Plataformas Web  
**Universidad Politécnica Salesiana**

---

## 1. Verificación de Java
Se verificó la instalación del entorno de ejecución de Java, confirmando la versión compatible (Java 25) para correr el framework.

![Evidencia de versión de Java](src/assets/Java_version.png)

## 2. Servidor Spring Boot en ejecución
El proyecto fue compilado usando Gradle. Se inicializó el servidor embebido Tomcat en el puerto 8080 sin necesidad de configuraciones externas.

![Evidencia del servidor Tomcat](src/assets/tomcat.png)

## 3. Prueba del Endpoint `/api/status`
Se realizó una petición HTTP tipo GET desde el navegador hacia la ruta del controlador, obteniendo una respuesta exitosa con código 200 OK y el Body en formato JSON.

![Evidencia del JSON en navegador](src/assets/navegador.png)

## 4. Estructura de Controladores
Verificación de la creación correcta del paquete y la clase del controlador mediante línea de comandos.

![Evidencia de la estructura de carpetas](src/assets/carpeta.png)

---

## 5. Configuración Global con application.yml
Durante la práctica, se migró el archivo de configuración de `application.properties` a `application.yml` para utilizar una sintaxis más limpia, estructurada y jerárquica. Se configuró el puerto del servidor y se estableció un prefijo global para la API:

```yaml
server: 
    port: 8080
    servlet:
        context-path: /api

spring: 
    application:
        name: fundamentos01
```
Al definir `/api` como el `context-path`, todas las rutas de la aplicación incluyen automáticamente este prefijo de forma estandarizada.

## 6. Modelo de Dominio y Controlador (Students)
Se implementó una nueva estructura modular organizando el código por dominios (dentro del paquete `students` se crearon los subpaquetes `models` y `controllers`).

* **Model (`Student.java`):** Define la estructura de datos del estudiante con sus atributos (`id`, `name`, `age`), constructores y métodos encapsulados (getters y setters).
* **Controller (`StudentController.java`):** Gestiona las peticiones HTTP relacionadas con los estudiantes. Se utilizó `@RestController` y se agruparon las rutas bajo `@RequestMapping("/students")`. Además, se inicializó una lista simulando una base de datos en memoria para retornar los datos.

## 7. Prueba de los Endpoints de Estudiantes
Se probaron los nuevos endpoints mapeados en el controlador mediante peticiones HTTP GET:

**Endpoint `/api/students`:** Retorna la lista completa de objetos `Student` serializados automáticamente en formato JSON.
![Lista de Estudiantes](src/assets/api-students.png)

**Endpoint `/api/students/count`:** Retorna un texto plano (String) con el conteo total de estudiantes registrados en la lista.
![Conteo de Estudiantes](src/assets/api-students-count.png)

---

## 8. Explicación y Conclusiones

**Sobre el funcionamiento de los endpoints:** Entendí que un endpoint es una ruta específica de comunicación que el servidor expone al exterior. Al configurar la clase con `@RestController`, `@RequestMapping` y los métodos con `@GetMapping`, logramos que cuando el cliente (navegador web) realiza una petición HTTP GET a esas rutas, el servidor ejecute la lógica interna y retorne automáticamente una respuesta estructurada (JSON o texto plano), sin necesidad de devolver una página HTML completa.

**Sobre la función general de Spring Boot:** Comprendí que Spring Boot simplifica radicalmente la creación de backends gracias a su auto-configuración y al uso de servidores embebidos. Al agregar la dependencia `spring-boot-starter-web` en el archivo `build.gradle`, el framework integró e inició automáticamente Apache Tomcat en el puerto 8080. Esto elimina la necesidad de instalar, configurar y desplegar servidores externos manualmente, permitiéndonos enfocarnos directamente en la lógica de negocio, la creación de modelos y la construcción de la API.

# Práctica 3: Construcción de una API REST usando DTOs y Mappers

En esta práctica se estructuró el flujo de datos utilizando DTOs (Data Transfer Objects) para controlar lo que ingresa y sale de la API, separando la lógica mediante Mappers. 

Se realizaron las pruebas de los endpoints estructurados (usando el recurso de usuarios/productos) para confirmar la correcta respuesta del servidor mediante el cliente Bruno.

**GET Global (Lista completa):** Retorna el arreglo JSON con todos los registros activos.
![GET Global](src/assets/get_global.png)

**GET por ID:** Retorna el JSON de un registro específico solicitado por su ID.
![GET por ID](src/assets/get_id.png)

**DELETE (Exitoso):** Eliminación correcta de un registro existente devolviendo un estado 200 OK.
![DELETE Exitoso](src/assets/delete.png)

**DELETE (Fallido):** Intento de eliminación de un registro que no existe, lanzando una excepción controlada (Status 500).
![DELETE Error](src/assets/delete_error.png)

---

# Práctica 4: Controladores + Servicios + Lógica de Negocio

Para mejorar la arquitectura, se sacó toda la lógica de negocio de los controladores y se delegó a la capa de servicios. 

**Capa de Servicio (`ProductServiceImpl.java`):** Se utiliza la anotación `@Service` para registrar la clase. Aquí se aplican las reglas de negocio, el uso de `.stream()` para las búsquedas y el mapeo de datos.
![Capa de Servicio](src/assets/ProductServiceImpl.java.png)

**Capa de Controlador (`ProductsController.java`):** Queda completamente limpio de lógica. Solo define las rutas y delega las tareas al servicio.
![Capa de Controlador](src/assets/ProductController.java.png)

### ¿Cómo se inyecta el servicio en el controlador?
Se realiza mediante **Inyección de Dependencias por Constructor**. Al declarar la variable `private final ProductService service;` y pasarla en el constructor del controlador, Spring Boot busca automáticamente la clase que tiene la anotación `@Service` (`ProductServiceImpl`), crea la instancia por nosotros y la inyecta para que esté lista para usarse.

---

# Práctica 5: Persistencia real con PostgreSQL y Repositorios

Se eliminó el almacenamiento temporal en memoria y se integró una base de datos real usando Docker, PostgreSQL y Spring Data JPA para asegurar la persistencia de la información.

**Registros guardados en PostgreSQL (Tabla Products):**
![Tabla Products](src/assets/products.png)

**Registros guardados en PostgreSQL (Tabla Users):**
![Tabla Users](src/assets/users.png)

### Flujo de datos y el uso de BaseEntity
El flujo de datos funciona así: la petición HTTP llega como JSON al **Controlador**, el cual la transforma en un DTO. El **Servicio** recibe el DTO, aplica la lógica y lo convierte en una Entidad (Entity) mediante el Mapper. Finalmente, el **Repositorio** toma esta entidad y usa Hibernate para generar la sentencia SQL que guarda los datos físicamente en **PostgreSQL**. Cuando se consultan datos, el viaje es a la inversa (BD -> Entidad -> Modelo -> DTO -> Cliente).

**Uso de `BaseEntity`:** Es una superclase clave (marcada con `@MappedSuperclass`) que agrupa los campos de auditoría (`id`, `createdAt`, `updatedAt`, `deleted`). Al hacer que las entidades como `UserEntity` o `ProductEntity` hereden de ella, evitamos repetir código en cada tabla y estandarizamos la generación de IDs y el borrado lógico en todo el proyecto.
# Práctica 6: Validación de DTOs y Control de Datos de Entrada

En esta práctica se integró Jakarta Validation para proteger la API de datos incorrectos antes de que lleguen a la capa de servicios o base de datos. Se implementaron anotaciones como `@NotBlank`, `@NotNull`, `@Size` y `@Min` en los DTOs, y se activaron mediante la anotación `@Valid` en los controladores.

Además, se implementaron reglas de negocio estrictas en los servicios para evitar la interacción con registros eliminados lógicamente.

**1. Error por validación de entrada (POST inválido):**
El controlador detiene la petición y devuelve un estado `400 Bad Request` al intentar enviar un nombre vacío y valores negativos para precio y stock.
![POST Inválido](src/assets/post_invalido.png)

**2. Regla de negocio: Bloqueo de actualización a producto eliminado:**
Al intentar realizar un método `PUT` sobre un producto que previamente fue marcado como eliminado (deleted = true), el servicio lanza una excepción `IllegalStateException` controlada.
![Error al actualizar eliminado](src/assets/update_eliminado.png)

**3. Regla de negocio: `findAll` no devuelve productos eliminados:**
La lista global excluye automáticamente los registros eliminados lógicamente, demostrando la eficacia del filtrado por streams en el servicio.
![GET sin eliminados](src/assets/get_sin_eliminados.png)