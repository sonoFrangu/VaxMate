package it.uninsubria.vaxmate

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import it.uninsubria.vaxmate.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvFooter.text = Html.fromHtml(
            getString(R.string.footer_text),
            Html.FROM_HTML_MODE_LEGACY
        )

        binding.tvFooter.movementMethod = LinkMovementMethod.getInstance()

        val currentLang = AppCompatDelegate.getApplicationLocales().toLanguageTags()

        if (currentLang.contains("en")) {
            binding.btnLanguage.setImageResource(R.drawable.ic_flag_it)
        } else {
            binding.btnLanguage.setImageResource(R.drawable.ic_flag_en)
        }

        binding.btnLanguage.setOnClickListener {
            val newLang = if (currentLang.contains("en")) "it" else "en"

            val appLocales = LocaleListCompat.forLanguageTags(newLang)

            AppCompatDelegate.setApplicationLocales(appLocales)
        }

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
            startActivity(
                Intent(this, MainActivity::class.java)
            )
        }
    }
}