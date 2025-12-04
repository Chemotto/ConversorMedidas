# Conversor de Medidas

Una aplicación de Android moderna y robusta para realizar conversiones de unidades en tiempo real. Diseñada con Kotlin y siguiendo las mejores prácticas de desarrollo en Android.

## 📱 Características Principales

*   **Conversión en Tiempo Real:** Los cálculos se realizan instantáneamente mientras el usuario escribe o cambia de unidad.
*   **Múltiples Categorías:** Soporte completo para:
    *   📏 **Longitud:** Metros, Pies, Kilómetros, Millas, Yardas, Centímetros, Pulgadas, Milímetros.
    *   ⚖️ **Peso:** Kilogramos, Libras, Gramos, Onzas, Toneladas.
    *   💧 **Volumen:** Litros, Galones, Mililitros, Onzas Líquidas, Pintas, Cuartos.
    *   🌡️ **Temperatura:** Celsius, Fahrenheit, Kelvin.
*   **Interfaz Moderna:** Diseño limpio basado en Material Design, con uso de Chips para categorías y CardViews para agrupar el contenido.
*   **Robustez:** Motor de conversión inteligente capaz de resolver conversiones directas e indirectas (triangulación automática de unidades).

## 🛠️ Arquitectura y Diseño

El proyecto sigue una arquitectura limpia separando la lógica de negocio de la interfaz de usuario.

### Componentes Principales

1.  **`MainActivity.kt`**:
    *   Controlador de la UI.
    *   Gestiona la selección de categorías mediante `ChipGroup`.
    *   Escucha cambios en el texto (`TextWatcher`) y en los selectores (`OnItemSelectedListener`) para disparar conversiones automáticas.
    *   Actualiza la interfaz dinámicamente (Badges, Resultados, Detalles).

2.  **`ConversionUtils.kt`**:
    *   Objeto Singleton (Patrón Singleton) que actúa como la única fuente de verdad.
    *   Almacena un mapa de funciones de conversión (`Map<Pair<String, String>, (Double) -> Double>`).
    *   **Algoritmo de Pivote:** Si no existe una conversión directa entre A y B, busca automáticamente un camino intermedio (A -> Base -> B) para resolver la conversión sin necesidad de definir todas las combinaciones posibles manualmente.

3.  **`activity_main.xml`**:
    *   Layout principal utilizando `ConstraintLayout` para un diseño responsivo.
    *   Implementa componentes de Material Design como Chips y Cards.

## 🧪 Pruebas Unitarias

La integridad de los cálculos está asegurada mediante pruebas unitarias exhaustivas utilizando JUnit.

*   **Ubicación:** `app/src/test/java/com/chema/conversormedidas/ConversionUtilsTest.kt`
*   **Cobertura:**
    *   Conversiones estándar de todas las categorías.
    *   Casos límite (mismo origen y destino).
    *   Manejo de errores (conversiones no soportadas).

Para ejecutar las pruebas, abre Android Studio, haz clic derecho sobre la carpeta `test` y selecciona "Run tests".

## 🚀 Cómo empezar

1.  Clona este repositorio.
2.  Abre el proyecto en **Android Studio**.
3.  Espera a que Gradle sincronice las dependencias.
4.  Ejecuta la aplicación en un emulador o dispositivo físico.

## 📝 Requisitos

*   Android SDK (minSdk recomendado: 24+)
*   Kotlin 1.5+
*   Android Studio Arctic Fox o superior.

---
Desarrollado con ❤️ en Kotlin.