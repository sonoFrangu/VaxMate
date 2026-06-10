package it.uninsubria.vaxmate

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import it.uninsubria.vaxmate.databinding.ActivityDoctorLoginBinding

class DoctorLoginActivity : BaseActivity() {

    private lateinit var binding: ActivityDoctorLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDoctorLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupLanguageButton(binding.languageButton.btnLanguage)

        val testoBase = getString(R.string.no_account_register)
        val testoColorato = getString(R.string.register_word)
        val testoCompleto = "$testoBase $testoColorato"
        val spannableString = SpannableString(testoCompleto)
        val colorTurchese = ContextCompat.getColor(this, R.color.primary)
        spannableString.setSpan(
            ForegroundColorSpan(colorTurchese),
            testoBase.length + 1,
            testoCompleto.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvRegister.text = spannableString

        val dbManager = DatabaseManager()

        pulisciErroriDuranteScrittura()

        binding.btnDoLogin.setOnClickListener {

            val emailInserita = binding.etEmail.text.toString().trim()
            val passwordInserita = binding.etPassword.text.toString().trim()

            azzeraTuttiGliErrori()

            var tuttoValido = true

            if (emailInserita.isEmpty()) {
                binding.tilEmail.error = getString(R.string.error_empty)
                tuttoValido = false
            }

            if (passwordInserita.isEmpty()) {
                binding.tilPassword.error = getString(R.string.error_empty)
                tuttoValido = false
            }

            if (!tuttoValido) {
                return@setOnClickListener
            }

            binding.btnDoLogin.isEnabled = false
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

            dbManager.loginMedico(emailInserita, passwordInserita) { successo ->

                binding.btnDoLogin.isEnabled = true
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                if (successo) {
                    Log.d("VaxMate_Debug", "Login effettuato con successo!")
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Log.e("VaxMate_Debug", "Errore di login")
                    binding.tilEmail.error = getString(R.string.error_email_psw)
                    binding.tilPassword.error = getString(R.string.error_email_psw)
                }
            }
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        //TODO: se abbiamo voglia fare psw dimenticata
        binding.tvGuest.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
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