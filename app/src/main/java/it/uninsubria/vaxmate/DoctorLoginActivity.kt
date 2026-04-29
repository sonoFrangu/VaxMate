package it.uninsubria.vaxmate

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class DoctorLoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_login)

        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnDoLogin = findViewById<MaterialButton>(R.id.btnDoLogin)

        btnDoLogin.setOnClickListener {
            val emailInserita = etEmail.text.toString()
            val passwordInserita = etPassword.text.toString()

            Log.d("VaxMate_Debug", "--- TENTATIVO DI LOGIN ---")
            Log.d("VaxMate_Debug", "Email: $emailInserita")
            Log.d("VaxMate_Debug", "Password: $passwordInserita")
        }
    }
}