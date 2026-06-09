package it.uninsubria.vaxmate

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.Patterns
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import it.uninsubria.vaxmate.databinding.ActivityRegisterBinding

class RegisterActivity : BaseActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val dbManager = DatabaseManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupLanguageButton(binding.languageButton.btnLanguage)

        val testoBase = getString(R.string.already_have_account)
        val testoColorato = getString(R.string.login_word)
        val testoCompleto = "$testoBase $testoColorato"
        val spannableString = SpannableString(testoCompleto)
        val colorTurchese = ContextCompat.getColor(this, R.color.primary)
        spannableString.setSpan(
            ForegroundColorSpan(colorTurchese),
            testoBase.length + 1,
            testoCompleto.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvGoToLogin.text = spannableString

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
                binding.tilFirstName.error = getString(R.string.error_empty)
                tuttoValido = false
            }
            if (cognome.isEmpty()) {
                binding.tilLastName.error = getString(R.string.error_empty)
                tuttoValido = false
            }
            if (ospedale.isEmpty()) {
                binding.tilHospital.error = getString(R.string.error_hospital)
                tuttoValido = false
            }

            if (email.isEmpty()) {
                binding.tilRegisterEmail.error = getString(R.string.error_empty)
                tuttoValido = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.tilRegisterEmail.error = getString(R.string.error_email)
                tuttoValido = false
            }

            if (pass1.length < 6) {
                binding.tilRegisterPassword.error = getString(R.string.error_len_psw)
                tuttoValido = false
            }

            if (pass2.isEmpty() || pass1 != pass2) {
                binding.tilConfirmPassword.error = getString(R.string.error_psw)
                tuttoValido = false
            }

            if (!tuttoValido) {
                return@setOnClickListener
            }

            binding.btnDoRegister.isEnabled = false
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

            dbManager.creaAccountMedico(email, pass1, nome, cognome, ospedale) { successo ->

                binding.btnDoRegister.isEnabled = true
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                if (successo) {
                    Log.d("VaxMate_Debug", "Registrazione completata su Firebase")
                    mostraPopupSuccesso()
                } else {
                    Log.e("VaxMate_Debug", "Errore durante la registrazione su Firebase")
                    Toast.makeText(this, getString(R.string.error_register), Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.tvGoToLogin.setOnClickListener {
            startActivity(Intent(this, DoctorLoginActivity::class.java))
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
            .setTitle(getString(R.string.complete_register))
            .setMessage(getString(R.string.complete_msg))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.complete_btn)) { dialog, _ ->
                dialog.dismiss()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .show()
    }
}