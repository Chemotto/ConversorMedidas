package com.chema.conversormedidas

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

/**
 * Actividad principal de la aplicación.
 * Se encarga de gestionar la interfaz de usuario y de coordinar la lógica de conversión,
 * delegando los cálculos al objeto `ConversionUtils`.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var inputValue: EditText
    private lateinit var spinnerFrom: Spinner
    private lateinit var spinnerTo: Spinner
    private lateinit var txtResult: TextView
    private lateinit var tvResultUnit: TextView
    private lateinit var tvConversionDetail: TextView
    private lateinit var tvInputUnitBadge: TextView
    private lateinit var chipGroupCategories: ChipGroup

    // Mapa de unidades por categoría
    private val unitsByCategory = mapOf(
        "Longitud" to arrayOf("Metros", "Pies", "Kilómetros", "Millas", "Yardas", "Centímetros", "Pulgadas", "Milímetros"),
        "Peso" to arrayOf("Kilogramos", "Libras", "Gramos", "Onzas", "Toneladas Métricas", "Toneladas USA"),
        "Volumen" to arrayOf("Litros", "Galones", "Mililitros", "Onzas Líquidas", "Pintas", "Cuartos"),
        "Temperatura" to arrayOf("Celsius", "Fahrenheit", "Kelvin")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicialización de vistas
        inputValue = findViewById(R.id.inputValue)
        spinnerFrom = findViewById(R.id.spinnerFrom)
        spinnerTo = findViewById(R.id.spinnerTo)
        txtResult = findViewById(R.id.txtResult)
        tvResultUnit = findViewById(R.id.tvResultUnit)
        tvConversionDetail = findViewById(R.id.tvConversionDetail)
        tvInputUnitBadge = findViewById(R.id.tvInputUnitBadge)
        chipGroupCategories = findViewById(R.id.chipGroupCategories)

        // Configurar listeners de Chips
        chipGroupCategories.setOnCheckedChangeListener { group, checkedId ->
            val chip = group.findViewById<Chip>(checkedId)
            if (chip != null) {
                val category = chip.text.toString()
                updateSpinners(category)
            }
        }

        // Configurar TextWatcher para conversión en tiempo real
        inputValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                calculateConversion()
            }
        })

        // Configurar Listeners para Spinners
        val onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                calculateConversion()
                updateBadges()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        spinnerFrom.onItemSelectedListener = onItemSelectedListener
        spinnerTo.onItemSelectedListener = onItemSelectedListener

        // Inicialización por defecto (Longitud)
        updateSpinners("Longitud")
    }

    private fun updateSpinners(category: String) {
        val units = unitsByCategory[category] ?: return
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, units)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        
        spinnerFrom.adapter = adapter
        spinnerTo.adapter = adapter

        // Seleccionar por defecto unidades diferentes para mostrar algo interesante
        if (units.size > 1) {
            spinnerTo.setSelection(1) 
        }
    }
    
    private fun updateBadges() {
        val fromUnit = spinnerFrom.selectedItem?.toString() ?: ""
        val toUnit = spinnerTo.selectedItem?.toString() ?: ""
        
        tvInputUnitBadge.text = fromUnit
        tvResultUnit.text = toUnit
    }

    private fun calculateConversion() {
        val text = inputValue.text.toString()
        val fromUnit = spinnerFrom.selectedItem?.toString()
        val toUnit = spinnerTo.selectedItem?.toString()

        if (text.isNotEmpty() && fromUnit != null && toUnit != null) {
            val value = text.toDoubleOrNull()
            if (value != null) {
                val result = ConversionUtils.convert(value, fromUnit, toUnit)
                if (result != null) {
                    txtResult.text = String.format(Locale.getDefault(), "%.2f", result)
                    
                    // Actualizar detalle de conversión (1 unidad origen = X unidad destino)
                    val unitValue = ConversionUtils.convert(1.0, fromUnit, toUnit)
                    if (unitValue != null) {
                        tvConversionDetail.text = String.format(Locale.getDefault(), "Basado en conversión estándar · 1 %s = %.4f %s", fromUnit, unitValue, toUnit)
                    }
                } else {
                    // Fallback si ConversionUtils.convert falla directamente (aunque ahora debería funcionar para la mayoría por los tests)
                     // Si el usuario selecciona manualmente una combinación que no está en el mapa directo ni soportada
                     txtResult.text = "---"
                     tvConversionDetail.text = "Conversión no disponible"
                }
            } else {
                txtResult.text = "---"
                tvConversionDetail.text = ""
            }
        } else {
            txtResult.text = "0,00"
             tvConversionDetail.text = ""
        }
    }
}