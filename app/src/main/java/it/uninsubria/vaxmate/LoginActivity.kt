package it.uninsubria.vaxmate

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.graphics.Color
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.TextView

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val tvFooter = findViewById<TextView>(R.id.tvFooter)

        val text = "Continuando, accetti i Termini di Servizio e la Privacy Policy di VaxMate."
        val spannable = SpannableString(text)

        val termsText = "Termini di Servizio"
        val privacyText = "Privacy Policy"

        val termsStart = text.indexOf(termsText)
        val termsEnd = termsStart + termsText.length

        val privacyStart = text.indexOf(privacyText)
        val privacyEnd = privacyStart + privacyText.length

        // CLICK TERMS
        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                // TODO: apri schermata Termini
                // startActivity(Intent(this@LoginActivity, TermsActivity::class.java))
            }
        }, termsStart, termsEnd, 0)

        // CLICK PRIVACY
        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                // TODO: apri schermata Privacy
                // startActivity(Intent(this@LoginActivity, PrivacyActivity::class.java))
            }
        }, privacyStart, privacyEnd, 0)

        // stile (blu + underline)
        spannable.setSpan(ForegroundColorSpan(Color.BLUE), termsStart, termsEnd, 0)
        spannable.setSpan(UnderlineSpan(), termsStart, termsEnd, 0)

        spannable.setSpan(ForegroundColorSpan(Color.BLUE), privacyStart, privacyEnd, 0)
        spannable.setSpan(UnderlineSpan(), privacyStart, privacyEnd, 0)

        tvFooter.text = spannable
        tvFooter.movementMethod = LinkMovementMethod.getInstance()
        tvFooter.highlightColor = Color.TRANSPARENT
    }
}