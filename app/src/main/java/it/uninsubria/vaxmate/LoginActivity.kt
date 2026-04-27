package it.uninsubria.vaxmate

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.google.android.material.button.MaterialButton

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val tvFooter = findViewById<TextView>(R.id.tvFooter)
        val btnLanguage = findViewById<com.google.android.material.imageview.ShapeableImageView>(R.id.btnLanguage)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)

        tvFooter.text = Html.fromHtml(
            getString(R.string.footer_text),
            Html.FROM_HTML_MODE_LEGACY
        )

        val currentLang = AppCompatDelegate.getApplicationLocales().toLanguageTags()
        if (currentLang.contains("en")) {
            btnLanguage.setImageResource(R.drawable.ic_flag_it)
        } else {
            btnLanguage.setImageResource(R.drawable.ic_flag_en)
        }

        btnLanguage.setOnClickListener {
            val newLang = if (currentLang.contains("en")) "it" else "en"
            val appLocales = LocaleListCompat.forLanguageTags(newLang)
            AppCompatDelegate.setApplicationLocales(appLocales)
        }

        btnLogin.setOnClickListener {
            val intent = Intent(this, DoctorLoginActivity::class.java)
            startActivity(intent)
        }

        tvFooter.movementMethod = LinkMovementMethod.getInstance()
    }
}