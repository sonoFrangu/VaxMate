package it.uninsubria.vaxmate

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etFirstName = findViewById<TextInputEditText>(R.id.etFirstName)
        val etLastName = findViewById<TextInputEditText>(R.id.etLastName)
        val etEmail = findViewById<TextInputEditText>(R.id.etRegisterEmail)
        val etHospital = findViewById<MaterialAutoCompleteTextView>(R.id.etHospital)
        val etPassword = findViewById<TextInputEditText>(R.id.etRegisterPassword)
        val etConfirmPassword = findViewById<TextInputEditText>(R.id.etConfirmPassword)
        val btnRegister = findViewById<MaterialButton>(R.id.btnDoRegister)
        val tvGoToLogin = findViewById<TextView>(R.id.tvGoToLogin)

        // PROVA FATTA CON IA
        val ospedali = arrayOf("Ospedale di Circolo Varese", "Ospedale Del Ponte", "Ospedale di Como")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, ospedali)
        etHospital.setAdapter(adapter)

        // FIX: Forza l'apertura del menu al click
        etHospital.setOnClickListener {
            etHospital.showDropDown()
        }

        btnRegister.setOnClickListener {
            val pass1 = etPassword.text.toString()
            val pass2 = etConfirmPassword.text.toString()

            if (pass1 != pass2) {
                Log.e("VaxMate_Debug", "ERRORE: Le password non corrispondono!")
            } else {
                Log.d("VaxMate_Debug", "--- REGISTRAZIONE ---")
                Log.d("VaxMate_Debug", "Nome: ${etFirstName.text} ${etLastName.text}")
                Log.d("VaxMate_Debug", "Ospedale: ${etHospital.text}")
                Log.d("VaxMate_Debug", "Email: ${etEmail.text}")
            }
        }

        tvGoToLogin.setOnClickListener {
            finish()
        }
    }
}