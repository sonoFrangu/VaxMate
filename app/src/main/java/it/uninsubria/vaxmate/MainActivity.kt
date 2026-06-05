package it.uninsubria.vaxmate

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.uninsubria.vaxmate.databinding.ActivityMainBinding
import android.view.View

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val utenteCorrente = auth.currentUser

        if (utenteCorrente == null) {
            impostaSalutoOspite()
        } else {
            db.collection("Medici").document(utenteCorrente.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val cognome = document.getString("cognome") ?: ""
                        impostaSalutoMedico(cognome)
                    } else {
                        impostaSalutoMedico("")
                    }
                }
                .addOnFailureListener {
                    impostaSalutoMedico("")
                }
        }
    }

    private fun impostaSalutoOspite() {
        val salutoBase = "Benvenuto, "
        val ruolo = "Dottore"
        val testoCompleto = salutoBase + ruolo

        val spannableString = SpannableString(testoCompleto)
        val colorBlu = ContextCompat.getColor(this, R.color.primary)

        spannableString.setSpan(
            ForegroundColorSpan(colorBlu),
            salutoBase.length,
            testoCompleto.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.tvGreeting.text = spannableString
        binding.tvGreetingSubtitle.text = "Non so cosa mettere qua"
        mostraContenuto()
    }

    private fun impostaSalutoMedico(cognome: String) {
        val salutoBase = "Buongiorno, "
        val nomeDottore = "Dr. $cognome"
        val testoCompleto = salutoBase + nomeDottore

        val spannableString = SpannableString(testoCompleto)
        val colorBlu = ContextCompat.getColor(this, R.color.primary)

        spannableString.setSpan(
            ForegroundColorSpan(colorBlu),
            salutoBase.length,
            testoCompleto.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.tvGreeting.text = spannableString
        binding.tvGreetingSubtitle.text = "Pronto per registrare le somministrazioni di oggi."
        mostraContenuto()
    }

    private fun mostraContenuto() {
        binding.progressBar.visibility = View.GONE
        binding.mainContent.visibility = View.VISIBLE
    }
}