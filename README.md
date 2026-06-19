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