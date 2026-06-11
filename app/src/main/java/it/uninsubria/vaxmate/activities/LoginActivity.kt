package it.uninsubria.vaxmate.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import it.uninsubria.vaxmate.activities.MainActivity
import it.uninsubria.vaxmate.R
import it.uninsubria.vaxmate.databinding.ActivityLoginBinding

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val utenteLoggato = FirebaseAuth.getInstance().currentUser

        if (utenteLoggato != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val reteDisponibile = isNetworkAvailable()

        binding.btnLogin.isEnabled = reteDisponibile
        binding.btnRegister.isEnabled = reteDisponibile

        if (!reteDisponibile) {
            binding.btnLogin.setBackgroundColor(Color.GRAY)
            binding.btnRegister.setTextColor(Color.GRAY)
            binding.btnRegister.strokeColor = ColorStateList.valueOf(Color.GRAY)
            binding.btnRegister.iconTint = ColorStateList.valueOf(Color.GRAY)
            Toast.makeText(this, "Connessione internet assente. Login disabilitato.", Toast.LENGTH_LONG).show()
        }

        setupLanguageButton(binding.languageButton.btnLanguage)

        binding.btnLogin.setOnClickListener {
            startActivity(
                Intent(this, DoctorLoginActivity::class.java)
            )
        }

        binding.btnRegister.setOnClickListener {
            startActivity(
                Intent(this, RegisterActivity::class.java)
            )
        }

        binding.btnGuest.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(
                Intent(this, MainActivity::class.java)
            )
        }
    }
}