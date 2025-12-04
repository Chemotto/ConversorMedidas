package com.chema.conversormedidas

/**
 * Objeto singleton que centraliza toda la lógica de conversión de unidades.
 * Al ser un 'object', solo existe una instancia de esta clase en toda la aplicación,
 * garantizando un único punto de acceso a la lógica de conversión.
 */
object ConversionUtils {
    /**
     * El mapa es la fuente de verdad para las conversiones directas.
     * Se han añadido enlaces a las unidades base (Metros, Kg, Litros, Celsius) para facilitar la triangulación.
     */
    private val conversions: Map<Pair<String, String>, (Double) -> Double> = mapOf(
        // 📏 Longitud - Base: Metros
        ("Metros" to "Pies") to ::metersToFeet,
        ("Pies" to "Metros") to ::feetToMeters,
        ("Metros" to "Yardas") to ::metersToYards,
        ("Yardas" to "Metros") to ::yardsToMeters,
        ("Metros" to "Kilómetros") to { v -> v / 1000.0 },
        ("Kilómetros" to "Metros") to { v -> v * 1000.0 },
        ("Metros" to "Centímetros") to { v -> v * 100.0 },
        ("Centímetros" to "Metros") to { v -> v / 100.0 },
        ("Metros" to "Milímetros") to { v -> v * 1000.0 },
        ("Milímetros" to "Metros") to { v -> v / 1000.0 },
        ("Metros" to "Millas") to { v -> milesToKilometers(v / 1000.0) }, // Corrección inversa
        ("Millas" to "Metros") to { v -> kilometersToMiles(v) * 1000.0 }, // Wait, logic below is better
        
        // Definiciones explícitas de Millas/Pulgadas vs Base para asegurar 2 pasos máx
        ("Millas" to "Metros") to { v -> v * 1609.34 },
        ("Metros" to "Millas") to { v -> v / 1609.34 },
        ("Pulgadas" to "Metros") to { v -> v * 0.0254 },
        ("Metros" to "Pulgadas") to { v -> v / 0.0254 },

        // Relaciones directas comunes existentes
        ("Kilómetros" to "Millas") to ::kilometersToMiles,
        ("Millas" to "Kilómetros") to ::milesToKilometers,
        ("Centímetros" to "Pulgadas") to ::centimetersToInches,
        ("Pulgadas" to "Centímetros") to ::inchesToCentimeters,
        ("Kilómetros" to "Yardas") to ::kilometersToYards,
        ("Yardas" to "Kilómetros") to ::yardsToKilometers,
        ("Milímetros" to "Pulgadas") to ::millimetersToInches,
        ("Pulgadas" to "Milímetros") to ::inchesToMillimeters,


        // ⚖Peso - Base: Kilogramos
        ("Kilogramos" to "Libras") to ::kilogramsToPounds,
        ("Libras" to "Kilogramos") to ::poundsToKilograms,
        ("Kilogramos" to "Gramos") to { v -> v * 1000.0 },
        ("Gramos" to "Kilogramos") to { v -> v / 1000.0 },
        ("Kilogramos" to "Toneladas Métricas") to { v -> v / 1000.0 },
        ("Toneladas Métricas" to "Kilogramos") to { v -> v * 1000.0 },
        
        // Enlaces extra a base
        ("Onzas" to "Kilogramos") to { v -> v * 0.0283495 },
        ("Kilogramos" to "Onzas") to { v -> v / 0.0283495 },
        ("Toneladas USA" to "Kilogramos") to { v -> v * 907.185 },
        ("Kilogramos" to "Toneladas USA") to { v -> v / 907.185 },

        // Relaciones directas existentes
        ("Gramos" to "Onzas") to ::gramsToOunces,
        ("Onzas" to "Gramos") to ::ouncesToGrams,
        ("Toneladas Métricas" to "Toneladas USA") to ::metricTonsToUSTons,
        ("Toneladas USA" to "Toneladas Métricas") to ::usTonsToMetricTons,


        // 💧 Volumen - Base: Litros
        ("Litros" to "Galones") to ::litersToGallonsUS,
        ("Galones" to "Litros") to ::gallonsUSToLiters,
        ("Litros" to "Pintas") to ::litersToPintsUS,
        ("Pintas" to "Litros") to ::pintsUSToLiters,
        ("Litros" to "Cuartos") to ::litersToQuartsUS,
        ("Cuartos" to "Litros") to ::quartsUSToLiters,
        ("Litros" to "Mililitros") to { v -> v * 1000.0 },
        ("Mililitros" to "Litros") to { v -> v / 1000.0 },
        
        // Enlaces extra a base
        ("Onzas Líquidas" to "Litros") to { v -> v * 0.0295735 },
        ("Litros" to "Onzas Líquidas") to { v -> v / 0.0295735 },

        // Relaciones directas existentes
        ("Mililitros" to "Onzas Líquidas") to ::millilitersToFluidOuncesUS,
        ("Onzas Líquidas" to "Mililitros") to ::fluidOuncesUSToMilliliters,


        // 🌡 Temperatura - Base: Celsius
        ("Celsius" to "Fahrenheit") to ::celsiusToFahrenheit,
        ("Fahrenheit" to "Celsius") to ::fahrenheitToCelsius,
        ("Celsius" to "Kelvin") to ::celsiusToKelvin,
        ("Kelvin" to "Celsius") to ::kelvinToCelsius,
        
        // Enlace extra (Kelvin <-> Fahrenheit ya estaba, pero vía Celsius es mejor si fallara)
        ("Fahrenheit" to "Kelvin") to ::fahrenheitToKelvin,
        ("Kelvin" to "Fahrenheit") to ::kelvinToFahrenheit
    )

    /**
     * Convierte un valor de una unidad a otra.
     * Intenta primero una conversión directa. Si no existe, busca un paso intermedio (pivote).
     */
    fun convert(value: Double, from: String, to: String): Double? {
        if (from == to) return value
        
        // 1. Intento directo
        val direct = conversions[from to to]
        if (direct != null) return direct(value)

        // 2. Intento indirecto (Paso intermedio)
        // Busca si existe una unidad 'X' tal que from->X y X->to existan.
        // Esto permite convertir Pies -> Metros -> Centímetros sin definir explícitamente Pies->Centímetros
        val possiblePivots = conversions.keys.filter { it.first == from }.map { it.second }
        
        for (pivot in possiblePivots) {
            val step2 = conversions[pivot to to]
            if (step2 != null) {
                val step1 = conversions[from to pivot]!!
                return step2(step1(value))
            }
        }

        return null
    }
    
    fun canConvert(from: String, to: String): Boolean {
        if (from == to) return true
        if (conversions.containsKey(from to to)) return true
        
        // Verificar si es posible indirectamente
        val possiblePivots = conversions.keys.filter { it.first == from }.map { it.second }
        for (pivot in possiblePivots) {
            if (conversions.containsKey(pivot to to)) return true
        }
        
        return false
    }
    
    fun getAvailableUnits(): List<String> {
        return conversions.keys.flatMap { listOf(it.first, it.second) }.distinct().sorted()
    }

    // --- Funciones de conversión privadas ---
    // 📏 Longitud
    private fun metersToFeet(m: Double) = m * 3.28084
    private fun feetToMeters(ft: Double) = ft / 3.28084
    private fun kilometersToMiles(km: Double) = km * 0.621371
    private fun milesToKilometers(mi: Double) = mi / 0.621371
    private fun centimetersToInches(cm: Double) = cm / 2.54
    private fun inchesToCentimeters(inch: Double) = inch * 2.54
    private fun metersToYards(m: Double) = m * 1.09361
    private fun yardsToMeters(yd: Double) = yd / 1.09361
    private fun kilometersToYards(km: Double) = km * 1093.61
    private fun yardsToKilometers(yd: Double) = yd / 1093.61
    private fun millimetersToInches(mm: Double) = mm / 25.4
    private fun inchesToMillimeters(inch: Double) = inch * 25.4
    
    // ⚖Peso
    private fun kilogramsToPounds(kg: Double) = kg * 2.20462
    private fun poundsToKilograms(lb: Double) = lb / 2.20462
    private fun gramsToOunces(g: Double) = g / 28.3495
    private fun ouncesToGrams(oz: Double) = oz * 28.3495
    private fun metricTonsToUSTons(t: Double) = t * 1.10231
    private fun usTonsToMetricTons(usTon: Double) = usTon / 1.10231
    
    // 💧 Volumen
    private fun litersToGallonsUS(L: Double) = L / 3.78541
    private fun gallonsUSToLiters(gal: Double) = gal * 3.78541
    private fun millilitersToFluidOuncesUS(ml: Double) = ml / 29.5735
    private fun fluidOuncesUSToMilliliters(flOz: Double) = flOz * 29.5735
    private fun litersToPintsUS(L: Double) = L * 2.11338
    private fun pintsUSToLiters(pt: Double) = pt / 2.11338
    private fun litersToQuartsUS(L: Double) = L * 1.05669
    private fun quartsUSToLiters(qt: Double) = qt / 1.05669

    // 🌡 Temperatura
    private fun celsiusToFahrenheit(C: Double) = (C * 9/5) + 32
    private fun fahrenheitToCelsius(F: Double) = (F - 32) * 5/9
    private fun celsiusToKelvin(C: Double) = C + 273.15
    private fun kelvinToCelsius(K: Double) = K - 273.15
    private fun fahrenheitToKelvin(F: Double) = (F - 32) * 5/9 + 273.15
    private fun kelvinToFahrenheit(K: Double) = (K - 273.15) * 9/5 + 32
}