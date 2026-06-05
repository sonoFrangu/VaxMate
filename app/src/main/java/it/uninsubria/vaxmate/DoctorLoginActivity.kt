package it.uninsubria.vaxmate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import it.uninsubria.vaxmate.databinding.ActivityDoctorLoginBinding

class DoctorLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDoctorLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dbManager = DatabaseManager()

        pulisciErroriDuranteScrittura()

        binding.btnDoLogin.setOnClickListener {

            val emailInserita = binding.etEmail.text.toString().trim()
            val passwordInserita = binding.etPassword.text.toString().trim()

            azzeraTuttiGliErrori()

            var tuttoValido = true

            if (emailInserita.isEmpty()) {
                binding.tilEmail.error = "Campo obbligatorio"
                tuttoValido = false
            }

            if (passwordInserita.isEmpty()) {
                binding.tilPassword.error = "Campo obbligatorio"
                tuttoValido = false
            }

            if (!tuttoValido) {
                return@setOnClickListener
            }

            dbManager.loginMedico(emailInserita, passwordInserita) { successo ->
                if (successo) {
                    Log.d("VaxMate_Debug", "Login effettuato con successo!")
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Log.e("VaxMate_Debug", "Errore di login")
                    binding.tilEmail.error = "Email o password errati"
                    binding.tilPassword.error = "Email o password errati"
                }
            }
        }
    }

    private fun pulisciErroriDuranteScrittura() {
        binding.etEmail.doOnTextChanged { _, _, _, _ -> binding.tilEmail.error = null }
        binding.etPassword.doOnTextChanged { _, _, _, _ -> binding.tilPassword.error = null }
    }

    private fun azzeraTuttiGliErrori() {
        binding.tilEmail.error = null
        binding.tilPassword.error = null
    }
}