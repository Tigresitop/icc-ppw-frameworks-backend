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

## 5. Explicación y Conclusiones

**Sobre el funcionamiento del endpoint:** Entendí que un endpoint es una ruta específica de comunicación (en este caso `/api/status`) que el servidor expone al exterior. Al configurar la clase con `@RestController` y el método con `@GetMapping`, logramos que cuando el cliente (navegador web) realiza una petición HTTP GET a esa ruta, el servidor ejecute la lógica interna y retorne automáticamente una respuesta estructurada en formato JSON, sin necesidad de devolver una página HTML completa.

**Sobre la función general de Spring Boot:** Comprendí que Spring Boot simplifica radicalmente la creación de backends gracias a su auto-configuración y al uso de servidores embebidos. Al agregar la dependencia `spring-boot-starter-web` en el archivo `build.gradle`, el framework integró e inició automáticamente Apache Tomcat en el puerto 8080. Esto elimina la necesidad de instalar, configurar y desplegar servidores externos manualmente, permitiéndonos enfocarnos directamente en la lógica de negocio y en la construcción de la API.