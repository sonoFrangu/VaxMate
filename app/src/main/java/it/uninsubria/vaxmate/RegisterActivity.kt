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
        val etHospital = findViewById<MaterialAutoCompleteTextView>(R.id.etHospital)
        val etPassword = findViewById<TextInputEditText>(R.id.etRegisterPassword)
        val etConfirmPassword = findViewById<TextInputEditText>(R.id.etConfirmPassword)
        val btnRegister = findViewById<MaterialButton>(R.id.btnDoRegister)
        val tvGoToLogin = findViewById<TextView>(R.id.tvGoToLogin)

        // PROVA FATTA CON IA
        val ospedali = arrayOf("Ospedale di Circolo Varese", "Ospedale Del Ponte", "Ospedale di Como")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, ospedali)
        etHospital.setAdapter(adapter)

        btnRegister.setOnClickListener {
            val pass1 = etPassword.text.toString()
            val pass2 = etConfirmPassword.text.toString()

            if (pass1 != pass2) {
                // PROVA FATTA CON IA
                Log.e("VaxMate_Debug", "ERRORE: Le password non corrispondono!")
            } else {
                // PROVA FATTA CON IA
                Log.d("VaxMate_Debug", "--- NUOVA REGISTRAZIONE ---")
                Log.d("VaxMate_Debug", "Nome: ${etFirstName.text}")
                Log.d("VaxMate_Debug", "Cognome: ${etLastName.text}")
                Log.d("VaxMate_Debug", "Ospedale: ${etHospital.text}")
                Log.d("VaxMate_Debug", "Password ok!")
            }
        }

        tvGoToLogin.setOnClickListener {
            finish()
        }
    }
}