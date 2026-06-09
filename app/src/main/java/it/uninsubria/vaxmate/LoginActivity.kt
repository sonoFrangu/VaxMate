package it.uninsubria.vaxmate

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.google.firebase.auth.FirebaseAuth
import it.uninsubria.vaxmate.databinding.ActivityLoginBinding

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//      !TODO AUTO LOGIN
//        val utenteLoggato = FirebaseAuth.getInstance().currentUser
//
//        if (utenteLoggato != null) {
//            startActivity(Intent(this, MainActivity::class.java))
//            finish()
//            return
//        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val reteDisponibile = isNetworkAvailable()

        binding.btnLogin.isEnabled = reteDisponibile
        binding.btnRegister.isEnabled = reteDisponibile

        if (!reteDisponibile) {
            binding.btnLogin.setBackgroundColor(android.graphics.Color.GRAY)
            binding.btnRegister.setTextColor(android.graphics.Color.GRAY)
            binding.btnRegister.strokeColor = android.content.res.ColorStateList.valueOf(android.graphics.Color.GRAY)
            binding.btnRegister.iconTint = android.content.res.ColorStateList.valueOf(android.graphics.Color.GRAY)
            Toast.makeText(this, "Connessione internet assente. Login disabilitato.", Toast.LENGTH_LONG).show()
        }

        binding.tvFooter.text = Html.fromHtml(
            getString(R.string.footer_text),
            Html.FROM_HTML_MODE_LEGACY
        )

        binding.tvFooter.movementMethod = LinkMovementMethod.getInstance()

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