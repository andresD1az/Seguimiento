# Pokédex - Aplicación Android con Jetpack Compose

Aplicación móvil Android que consume datos de la **PokeAPI** y los almacena localmente, permitiendo consultas offline.

## 📋 Características

- ✅ Consumo de 4 endpoints de PokeAPI
- ✅ Persistencia local con Room (SQLite)
- ✅ Modo offline con datos almacenados
- ✅ Paginación con scroll infinito
- ✅ Búsqueda por nombre y filtrado por tipo
- ✅ Indicador de estado de conexión
- ✅ Arquitectura en capas con Hilt
- ✅ Jetpack Compose UI

## 🏗️ Arquitectura

La aplicación sigue una arquitectura en capas:

```
UI (Compose)
    ↓
ViewModel
    ↓
Repository (abstracción)
    ↓
Data Sources
    ├── Remote (Retrofit + PokeAPI)
    └── Local (Room + SQLite)
```

### Capas

1. **UI**: Pantallas Compose (`PokemonListScreen`, `PokemonDetailScreen`)
2. **ViewModel**: Lógica de presentación (`PokemonListViewModel`, `PokemonDetailViewModel`)
3. **Repository**: Abstracción de fuentes de datos (`PokemonRepository`)
4. **Domain**: Modelos de dominio (`Pokemon`, `PokemonSummary`)
5. **Data**: Implementación de repositorios, DAOs, API services

## 🌐 API Utilizada: PokeAPI

**Base URL**: `https://pokeapi.co/api/v2/`

### Endpoints Consumidos

| # | Endpoint | Método | Descripción | Parámetros | Respuesta |
|---|----------|--------|-------------|------------|-----------|
| 1 | `/pokemon` | GET | Lista paginada de Pokémon | `limit` (int), `offset` (int) | `PokemonListResponse` con array de resultados |
| 2 | `/pokemon/{nameOrId}` | GET | Detalle completo de un Pokémon | `nameOrId` (string/int) | `PokemonDetailResponse` con stats, tipos, habilidades, sprites |
| 3 | `/pokemon-species/{nameOrId}` | GET | Información de especie (descripción, categoría) | `nameOrId` (string/int) | `PokemonSpeciesResponse` con flavor text, genus, legendary status |
| 4 | `/type/{typeName}` | GET | Lista de Pokémon por tipo | `typeName` (string) | `PokemonTypeResponse` con array de Pokémon del tipo |

### Ejemplo de Respuestas

**Endpoint 1** (`/pokemon?limit=20&offset=0`):
```json
{
  "count": 1302,
  "next": "https://pokeapi.co/api/v2/pokemon?offset=20&limit=20",
  "results": [
    {"name": "bulbasaur", "url": "https://pokeapi.co/api/v2/pokemon/1/"}
  ]
}
```

**Endpoint 2** (`/pokemon/1`):
```json
{
  "id": 1,
  "name": "bulbasaur",
  "height": 7,
  "weight": 69,
  "types": [{"type": {"name": "grass"}}, {"type": {"name": "poison"}}],
  "stats": [{"base_stat": 45, "stat": {"name": "hp"}}],
  "sprites": {"front_default": "https://..."}
}
```

## 🗄️ Base de Datos SQLite (Room)

### Diagrama de Entidades

```
┌─────────────────────────────────────────┐
│           PokemonEntity                 │
├─────────────────────────────────────────┤
│ id: Int (PK)                            │
│ name: String                            │
│ imageUrl: String                        │
│ types: String (CSV: "fire,flying")      │
│ height: Int                             │
│ weight: Int                             │
│ baseExperience: Int                     │
│ abilities: String (CSV)                 │
│ stats: String (formato: "hp:45,atk:60") │
│ captureRate: Int                        │
│ isLegendary: Boolean                    │
│ isMythical: Boolean                     │
│ description: String                     │
│ genus: String                           │
│ pageIndex: Int                          │
└─────────────────────────────────────────┘
```

### Estrategia de Persistencia

- **Evitar redundancia**: `INSERT OR REPLACE` con ID como clave primaria
- **Consistencia**: Los datos remotos siempre sobrescriben los locales
- **Paginación**: Campo `pageIndex` mantiene el orden de carga
- **Normalización**: Tipos, habilidades y stats se almacenan como CSV/strings estructurados

### Queries Principales

```kotlin
// Paginación
@Query("SELECT * FROM pokemon ORDER BY id ASC")
fun getAllPokemonPaged(): PagingSource<Int, PokemonEntity>

// Búsqueda por nombre
@Query("SELECT * FROM pokemon WHERE name LIKE '%' || :query || '%'")
fun searchByName(query: String): Flow<List<PokemonEntity>>

// Filtro por tipo
@Query("SELECT * FROM pokemon WHERE types LIKE '%' || :type || '%'")
fun filterByType(type: String): Flow<List<PokemonEntity>>

// Búsqueda combinada
@Query("SELECT * FROM pokemon WHERE name LIKE '%' || :name || '%' AND types LIKE '%' || :type || '%'")
fun searchByNameAndType(name: String, type: String): Flow<List<PokemonEntity>>
```

## 🔄 Flujo de Datos

### Modo Online
1. Usuario abre la app
2. `PokemonRemoteMediator` detecta que hay conexión
3. Consume `/pokemon` para obtener lista
4. Para cada Pokémon, consume `/pokemon/{id}` y `/pokemon-species/{id}`
5. Mapea respuestas a `PokemonEntity`
6. Inserta en Room con `INSERT OR REPLACE`
7. UI muestra datos desde Room (fuente única de verdad)

### Modo Offline
1. Usuario abre la app sin conexión
2. `NetworkMonitor` detecta ausencia de red
3. Banner rojo indica "Sin conexión"
4. `PagingSource` carga datos directamente desde Room
5. Búsquedas y filtros funcionan sobre datos locales

## 📱 Funcionalidades

### Pantalla Principal (Lista)
- Grid de 2 columnas con tarjetas de Pokémon
- Scroll infinito con paginación automática
- Búsqueda por nombre (campo de texto)
- Filtro por tipo (dropdown con 18 tipos)
- Indicador de carga al final de la lista
- Banner de conectividad

### Pantalla de Detalle
- Imagen grande del Pokémon
- Información básica (altura, peso, exp. base, tasa de captura)
- Tipos con chips de colores
- Descripción de la Pokédex
- Habilidades
- Estadísticas base con barras de progreso
- Indicadores de Legendario/Mítico

## 🛠️ Tecnologías

| Categoría | Tecnología | Versión |
|-----------|-----------|---------|
| Lenguaje | Kotlin | 2.2.10 |
| UI | Jetpack Compose | BOM 2024.09.00 |
| Navegación | Navigation Compose | 2.9.0 |
| Inyección de Dependencias | Hilt | 2.56.1 |
| Red | Retrofit | 2.11.0 |
| Serialización | Gson | 2.11.0 |
| Base de Datos | Room | 2.7.1 |
| Paginación | Paging 3 | 3.3.6 |
| Imágenes | Coil | 2.7.0 |
| Async | Coroutines + Flow | - |

## 🚀 Instalación

1. Clonar el repositorio:
```bash
git clone <URL_DEL_REPOSITORIO>
```

2. Abrir en Android Studio (Hedgehog o superior)

3. Sincronizar Gradle:
```bash
./gradlew build
```

4. Ejecutar en emulador o dispositivo físico (API 24+)

## 🧪 Pruebas

### Probar Modo Offline
1. Abrir la app con conexión
2. Esperar a que cargue al menos 20 Pokémon
3. Activar modo avión
4. Cerrar y reabrir la app
5. Verificar que muestra datos almacenados

### Probar Búsqueda
- Buscar "char" → debe mostrar Charmander, Charmeleon, Charizard
- Filtrar por tipo "fire" → debe mostrar todos los Pokémon de fuego

### Probar Paginación
- Hacer scroll hasta el final
- Verificar que aparece indicador de carga
- Confirmar que se cargan más Pokémon automáticamente

## 🤖 Uso de Inteligencia Artificial

### Herramientas Utilizadas

**Kiro AI Agent** (CLI/IDE Integration)
- **Propósito**: Generación de código, arquitectura y scaffolding del proyecto
- **Integración**: Agente autónomo con acceso a herramientas de desarrollo
- **Tareas realizadas**:
  - Configuración inicial de Gradle con todas las dependencias
  - Generación de estructura de capas (UI, ViewModel, Repository, Data, Domain)
  - Implementación de Room DAOs, Entities y Database
  - Creación de Retrofit API service y modelos de respuesta
  - Desarrollo de ViewModels con StateFlow y Paging
  - Diseño de UI con Jetpack Compose (pantallas, componentes reutilizables)
  - Configuración de Hilt para inyección de dependencias
  - Implementación de `NetworkMonitor` para detección de conectividad
  - Creación de `PokemonRemoteMediator` para paginación con caché

### Flujo de Trabajo con IA

1. **Análisis de Requerimientos**: El agente analizó los requisitos del taller
2. **Diseño de Arquitectura**: Propuso arquitectura en capas con separación de responsabilidades
3. **Generación de Código**: Creó todos los archivos Kotlin necesarios
4. **Configuración de Build**: Actualizó `build.gradle.kts` y `libs.versions.toml`
5. **Integración**: Conectó todas las capas (API → Repository → ViewModel → UI)
6. **Documentación**: Generó este README con diagramas y explicaciones

### Ventajas del Uso de IA

- ⚡ **Velocidad**: Proyecto completo generado en minutos
- 🎯 **Consistencia**: Código sigue patrones y convenciones uniformes
- 📚 **Best Practices**: Implementa arquitectura recomendada por Google
- 🔧 **Configuración**: Manejo automático de dependencias y versiones
- 📖 **Documentación**: README detallado generado automáticamente

## 📸 Capturas de Pantalla

*(Incluir aquí capturas de pantalla de la aplicación funcionando)*

1. **Pantalla principal con lista de Pokémon**
2. **Búsqueda por nombre**
3. **Filtro por tipo**
4. **Pantalla de detalle**
5. **Modo offline (banner rojo)**
6. **Paginación en acción**

## 📄 Licencia

Este proyecto es de uso educativo para el taller de desarrollo Android.

## 👨‍💻 Autor

Desarrollado con asistencia de Kiro AI Agent para el curso de Desarrollo Móvil Android.
