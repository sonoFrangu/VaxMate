package it.uninsubria.vaxmate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

        binding.btnDoRegister.setOnClickListener {

            val nome = binding.etFirstName.text.toString()
            val cognome = binding.etLastName.text.toString()
            val email = binding.etRegisterEmail.text.toString()
            val pass1 = binding.etRegisterPassword.text.toString()
            val pass2 = binding.etConfirmPassword.text.toString()
            val ospedale = binding.etHospital.text.toString()

            if (nome.isEmpty() || cognome.isEmpty() || email.isEmpty() || pass1.isEmpty() || ospedale.isEmpty()) {
                Toast.makeText(this, "Compila tutti i campi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Inserisci un indirizzo email valido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pass1.length < 6) {
                Toast.makeText(this, "La password deve contenere almeno 6 caratteri", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pass1 != pass2) {
                Log.e("VaxMate_Debug", "Password diverse")
                Toast.makeText(this, "Le password non coincidono", Toast.LENGTH_SHORT).show()
            } else {
                dbManager.creaAccountMedico(email, pass1, nome, cognome, ospedale) { successo ->
                    if (successo) {
                        Log.d("VaxMate_Debug", "Registrazione completata su Firebase")
                        mostraPopupSuccesso()
                    } else {
                        Log.e("VaxMate_Debug", "Errore durante la registrazione su Firebase")
                        Toast.makeText(this, "Errore di registrazione", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.tvGoToLogin.setOnClickListener {
            finish()
        }
    }


    //!TODO
    // CELE CONTROLLAMI QUESTO CHE NON MI CONVINCE, FATTO TOTALMENTE DA GEMINI QUESTO
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