package com.chema.conversormedidas

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Clase de pruebas unitarias para ConversionUtils.
 * Cada función anotada con @Test verifica un escenario de conversión.
 */
class ConversionUtilsTest {
    // Delta de precisión para comparar valores Double.
    // Se usa para evitar errores por la imprecisión de los números de punto flotante.
    private val delta = 0.001

    @Test
    fun testMetersToFeetConversionIsCorrect() {
        // 1 metro son aproximadamente 3.28084 pies
        assertEquals(3.28084, ConversionUtils.convert(1.0, "Metros", "Pies")!!, delta)
    }

    @Test
    fun testKilometersToMilesConversionIsCorrect() {
        // 10 kilómetros son aproximadamente 6.21371 millas
        assertEquals(6.21371, ConversionUtils.convert(10.0, "Kilómetros", "Millas")!!, delta)
    }

    @Test
    fun testCelsiusToFahrenheitConversionIsCorrect() {
        // 0 grados Celsius son 32 Fahrenheit
        assertEquals(32.0, ConversionUtils.convert(0.0, "Celsius", "Fahrenheit")!!, delta)
        // 100 grados Celsius son 212 Fahrenheit
        assertEquals(212.0, ConversionUtils.convert(100.0, "Celsius", "Fahrenheit")!!, delta)
    }

    @Test
    fun testPoundsToKilogramsConversionIsCorrect() {
        // 10 libras son aproximadamente 4.53592 kilogramos
        assertEquals(4.53592, ConversionUtils.convert(10.0, "Libras", "Kilogramos")!!, delta)
    }

    @Test
    fun testUnsupportedConversionReturnsNull() {
        // Una conversión no definida en el mapa (ej: Metros a Kilogramos) debe devolver null.
        // Aquí no se usa delta porque comparamos con null.
        assertEquals(null, ConversionUtils.convert(1.0, "Metros", "Kilogramos"))
    }

    @Test
    fun testSameUnitConversionReturnsOriginalValue() {
        // La conversión de una unidad a sí misma no debe cambiar el valor.
        assertEquals(123.0, ConversionUtils.convert(123.0, "Metros", "Metros")!!, delta)
        assertEquals(99.0, ConversionUtils.convert(99.0, "Libras", "Libras")!!, delta)
    }
}