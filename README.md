# KonoCloud

## Descripción
KonoCloud es una aplicación móvil desarrollada para Android diseñada para organizar y gestionar tareas escolares de manera eficiente. La aplicación se conecta a una API RESTful construida con FastAPI y alojada en una instancia de AWS EC2.

## Características Principales
- **Autenticación completa:** Registro, inicio y cierre de sesión seguros.
- **Persistencia de sesión:** Almacenamiento local de tokens y datos de usuario mediante SharedPreferences.
- **CRUD de Tareas:** Creación, lectura (listado y detalle), actualización (marcar completada/pendiente o editar) y eliminación de tareas.
- **Filtros y Progreso:** Filtrado de tareas (Todas, Pendientes, Completadas) y cálculo dinámico del progreso con barra indicadora.
- **Prioridades y Estados:** Clasificación por prioridades (Alta, Media, Baja) con indicadores visuales y etiquetas de estado.
- **Adaptabilidad y Teclado:** Formularios con soporte completo para desplazamiento, gestión de IME padding e interacciones fluidas en orientaciones vertical y horizontal.

## Tecnologías Utilizadas
- **Lenguaje:** Kotlin
- **Interfaz de Usuario:** Jetpack Compose, Material 3 (paleta clara fija)
- **Arquitectura:** MVVM (Model-View-ViewModel) con `StateFlow` y `collectAsStateWithLifecycle()`
- **Red y Serialización:** Retrofit, OkHttp, `kotlinx.serialization`
- **Inyección de Dependencias:** Contenedor manual (`AppContainer` y `AppViewModelFactory`)
- **Backend:** FastAPI, Python, SQLite (alojado en AWS EC2)

## Configuración y Ejecución

### 1. Clonar el Repositorio
```bash
git clone https://github.com/Alekiwuichip23/kono-cloud.git
```

### 2. Abrir en Android Studio
Abre Android Studio, selecciona **Open an Existing Project** y elige la carpeta raíz del proyecto clonado `KonoCloud`. Espera a que finalice la sincronización de Gradle.

### 3. Configurar la URL de la API
En la raíz del proyecto, asegúrate de tener (o crear) el archivo `local.properties` (que se encuentra excluido del control de versiones) y define la variable `API_BASE_URL` apuntando a tu servidor FastAPI o túnel (ej. Cloudflare / ngrok / IP del servidor), conservando `sdk.dir`:

```properties
sdk.dir=C\:\\Users\\TU_USUARIO\\AppData\\Local\\Android\\Sdk
API_BASE_URL=https://tu-api.konocloud.com/
```

### 4. Compilar y Ejecutar
Conecta tu dispositivo Android por USB (con la depuración USB habilitada) o inicia un emulador, y ejecuta la app desde Android Studio o mediante la terminal:

```bash
./gradlew assembleDebug
```

## Autora

Alessandra de los Santos Beltrán
