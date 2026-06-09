package it.uninsubria.vaxmate

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.google.android.material.imageview.ShapeableImageView

open class BaseActivity : AppCompatActivity() {

    protected fun setupLanguageButton(
        button: ShapeableImageView
    ) {
        val currentLang = AppCompatDelegate.getApplicationLocales().toLanguageTags()

        if (currentLang.contains("en")) {
            button.setImageResource(R.drawable.ic_flag_it)
        } else {
            button.setImageResource(R.drawable.ic_flag_en)
        }

        button.setOnClickListener {
            val newLang = if (currentLang.contains("en")) "it" else "en"

            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(newLang))
        }
    }
}