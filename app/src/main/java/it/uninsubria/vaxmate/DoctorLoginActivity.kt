package it.uninsubria.vaxmate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import it.uninsubria.vaxmate.databinding.ActivityDoctorLoginBinding

class DoctorLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDoctorLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dbManager = DatabaseManager()

        binding.btnDoLogin.setOnClickListener {

            val emailInserita = binding.etEmail.text.toString().trim()
            val passwordInserita = binding.etPassword.text.toString().trim()

            if (emailInserita.isEmpty() || passwordInserita.isEmpty()) {
                android.widget.Toast.makeText(this, "Inserisci email e password", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            dbManager.loginMedico(emailInserita, passwordInserita) { successo ->
                if (successo) {
                    Log.d("VaxMate_Debug", "Login effettuato con successo!")
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Log.e("VaxMate_Debug", "Errore di login")
                    android.widget.Toast.makeText(this, "Email o password errati", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}