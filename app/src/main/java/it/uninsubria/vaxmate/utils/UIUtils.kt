package it.uninsubria.vaxmate.utils

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import it.uninsubria.vaxmate.R

object UIUtils {
    
    fun creaSalutoColorato(context: Context, salutoBase: String, nomeAggiuntivo: String): SpannableString {
        val testoCompleto = "$salutoBase $nomeAggiuntivo"
        val spannableString = SpannableString(testoCompleto)
        val colorBlu = ContextCompat.getColor(context, R.color.primary)

        spannableString.setSpan(
            ForegroundColorSpan(colorBlu),
            salutoBase.length,
            testoCompleto.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return spannableString
    }
}