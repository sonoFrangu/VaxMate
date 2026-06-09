package it.uninsubria.vaxmate

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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

    protected fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}