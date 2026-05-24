# Resumen de la Implementación del Sistema de Alojamientos

¡El backend para la gestión de alojamientos está completamente implementado y compila correctamente! 🎉

## Arquitectura y Patrones Aplicados (GRASP)
Se ha seguido estrictamente el diseño conceptual provisto y los patrones GRASP:
- **Experto en Información**: Se ha asignado la responsabilidad de la lógica de negocio a las entidades correspondientes (por ejemplo, `PoliticaCancelacion` calcula los reembolsos, `Disponibilidad` verifica si hay solapamiento de fechas, y `Alojamiento` valida los campos al ser publicado).
- **Creador**: Los servicios (como `ServicioReservas` y `ServicioAnuncios`) se encargan de instanciar los objetos del dominio y persistirlos utilizando los repositorios.
- **Invención Pura (Indirección)**: Se introdujo la clase `ResultadoBusqueda` como intermediario para desacoplar la lógica de búsqueda de la capa de presentación.
- **Variaciones Protegidas**: Se preparó la estructura para que los pagos y notificaciones sean fácilmente mockeables o reemplazables en el futuro.
- **Controlador**: Se han creado los REST Controllers (`ControladorBusqueda`, `ControladorReserva`, `ControladorPublicacion`) para delegar las llamadas a los servicios correspondientes.

> [!TIP]
> **Concurrencia en Búsquedas**
> El `ServicioBusqueda` implementa concurrencia utilizando `CompletableFuture` para poder realizar búsquedas en paralelo (simulando búsquedas en distintas zonas o con distintos filtros simultáneamente), mejorando el rendimiento general.

## ¿Qué incluye la entrega actual?
1. **Modelos de Dominio (Entities)** configurados con JPA e Hibernate.
2. **Repositorios** para acceder a la base de datos en memoria (H2).
3. **Servicios** con toda la lógica de los Casos de Uso principales (CU-01 Reserva, CU-03 Cancelación, CU-04 Publicación, y Búsqueda concurrente).
4. **Controladores REST** que exponen esta funcionalidad.
5. **Carga de datos inicial** a través del archivo `DataInitializer.java` para que puedas probar la aplicación con datos pre-cargados al iniciarla.

## 📦 Instrucciones para entregar SOLO el Backend

Como mencionaste que en esta práctica no es obligatorio entregar el Frontend, y solo quieres presentar el Backend (código Java), aquí tienes los pasos para limpiar el proyecto antes de comprimirlo:

1. Ve a la carpeta `src/main/resources/static`.
2. **Elimina todos los archivos** que se encuentren allí (`index.html`, `styles.css`, `app.js`). Esto eliminará completamente la interfaz gráfica.
3. Asegúrate de eliminar también la carpeta `jdk17` que hemos descargado para compilar localmente y la carpeta `build/` (o `.gradle/`) para que el archivo final no pese demasiado.
4. Selecciona la carpeta raíz de tu proyecto (que contendrá `src/`, `build.gradle`, etc.) y **comprímela en un archivo .zip**.

> [!NOTE]
> Si decides probar el proyecto con el front-end antes de borrarlo, puedes hacerlo ejecutando la aplicación desde tu IDE y visitando `http://localhost:8080` en tu navegador. Una vez termines de probar, simplemente elimina el contenido de `static/`.

¡El código está listo para tu revisión y entrega! Si tienes alguna duda sobre alguna de las clases generadas o necesitas algún ajuste adicional, no dudes en decirme.

---

## 🚀 Cómo probar el proyecto (para tus compañeros)

Si te has descargado este repositorio y quieres probarlo en tu ordenador, solo tienes que seguir estos sencillos pasos:

### 1. Requisitos previos
- Necesitas tener **Java 17** (o superior) instalado en tu ordenador.
- Un IDE que soporte Java como **IntelliJ IDEA** (recomendado), **Eclipse**, o **VS Code** (con el *Extension Pack for Java*).

### 2. Pasos para ejecutarlo
1. **Abre el proyecto**: Descomprime el `.zip` o clona el repositorio, y abre la carpeta raíz (`Alojamientos`) en tu IDE.
2. **Deja que Gradle se configure**: El IDE detectará automáticamente que es un proyecto Gradle y descargará todas las dependencias necesarias (Spring Boot, H2, etc.). Esto puede tardar uno o dos minutos la primera vez.
3. **Arranca la aplicación**:
   - Busca el archivo `AlojamientosApplication.java` dentro de `src/main/java/com/grupom/alojamientos/`.
   - Haz clic derecho sobre este archivo y selecciona **Run 'AlojamientosApplication.main()'**.
   - *(Alternativa por consola)*: Puedes abrir la terminal en la raíz del proyecto y escribir `./gradlew bootRun` (en Mac/Linux) o `.\gradlew.bat bootRun` (en Windows).
4. **Verifica que está funcionando**: Sabrás que está encendido cuando veas en la consola un mensaje que diga algo como `Tomcat started on port 8080 (http)`.

### 3. ¡Pruébalo!
- Abre tu navegador web favorito y entra a **[http://localhost:8080](http://localhost:8080)**.
- El proyecto ya cuenta con una base de datos en memoria (H2) que se inicializa automáticamente con datos de prueba gracias a la clase `DataInitializer.java`. Podrás buscar alojamientos, cancelar reservas y probar todo el flujo nada más arrancar el servidor.
