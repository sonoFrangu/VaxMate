package it.uninsubria.vaxmate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import it.uninsubria.vaxmate.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val dbManager = DatabaseManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ospedali = arrayOf(
            "Ospedale di Circolo Varese",
            "Ospedale Del Ponte",
            "Ospedale di Como"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            ospedali
        )

        binding.etHospital.setAdapter(adapter)

        binding.etHospital.setOnClickListener {
            binding.etHospital.showDropDown()
        }

        pulisciErroriDuranteScrittura()

        binding.btnDoRegister.setOnClickListener {
            val nome = binding.etFirstName.text.toString().trim()
            val cognome = binding.etLastName.text.toString().trim()
            val email = binding.etRegisterEmail.text.toString().trim()
            val pass1 = binding.etRegisterPassword.text.toString().trim()
            val pass2 = binding.etConfirmPassword.text.toString().trim()
            val ospedale = binding.etHospital.text.toString().trim()

            azzeraTuttiGliErrori()

            var tuttoValido = true

            if (nome.isEmpty()) {
                binding.tilFirstName.error = "Campo obbligatorio"
                tuttoValido = false
            }
            if (cognome.isEmpty()) {
                binding.tilLastName.error = "Campo obbligatorio"
                tuttoValido = false
            }
            if (ospedale.isEmpty()) {
                binding.tilHospital.error = "Seleziona un ospedale"
                tuttoValido = false
            }

            if (email.isEmpty()) {
                binding.tilRegisterEmail.error = "Campo obbligatorio"
                tuttoValido = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.tilRegisterEmail.error = "Email non valida"
                tuttoValido = false
            }

            if (pass1.length < 6) {
                binding.tilRegisterPassword.error = "Minimo 6 caratteri"
                tuttoValido = false
            }

            if (pass2.isEmpty() || pass1 != pass2) {
                binding.tilConfirmPassword.error = "Le password non coincidono"
                tuttoValido = false
            }

            if (!tuttoValido) {
                return@setOnClickListener
            }

            dbManager.creaAccountMedico(email, pass1, nome, cognome, ospedale) { successo ->
                if (successo) {
                    Log.d("VaxMate_Debug", "Registrazione completata su Firebase")
                    mostraPopupSuccesso()
                } else {
                    Log.e("VaxMate_Debug", "Errore durante la registrazione su Firebase")
                    Toast.makeText(this, "Errore di registrazione (Es. email già in uso)", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.tvGoToLogin.setOnClickListener {
            finish()
        }
    }

    private fun pulisciErroriDuranteScrittura() {
        binding.etFirstName.doOnTextChanged { _, _, _, _ -> binding.tilFirstName.error = null }
        binding.etLastName.doOnTextChanged { _, _, _, _ -> binding.tilLastName.error = null }
        binding.etRegisterEmail.doOnTextChanged { _, _, _, _ -> binding.tilRegisterEmail.error = null }
        binding.etHospital.doOnTextChanged { _, _, _, _ -> binding.tilHospital.error = null }
        binding.etRegisterPassword.doOnTextChanged { _, _, _, _ -> binding.tilRegisterPassword.error = null }
        binding.etConfirmPassword.doOnTextChanged { _, _, _, _ -> binding.tilConfirmPassword.error = null }
    }
    private fun azzeraTuttiGliErrori() {
        binding.tilFirstName.error = null
        binding.tilLastName.error = null
        binding.tilRegisterEmail.error = null
        binding.tilHospital.error = null
        binding.tilRegisterPassword.error = null
        binding.tilConfirmPassword.error = null
    }

    private fun mostraPopupSuccesso() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Registrazione Completata")
            .setMessage("Il tuo account medico è stato creato con successo.")
            .setCancelable(false)
            .setPositiveButton("Vai all'App") { dialog, _ ->
                dialog.dismiss()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .show()
    }
}