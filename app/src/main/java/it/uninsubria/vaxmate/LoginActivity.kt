package it.uninsubria.vaxmate

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val tvFooter = findViewById<TextView>(R.id.tvFooter)

        tvFooter.text = Html.fromHtml(
            getString(R.string.footer_text),
            Html.FROM_HTML_MODE_LEGACY
        )

        tvFooter.movementMethod = LinkMovementMethod.getInstance()
    }
}