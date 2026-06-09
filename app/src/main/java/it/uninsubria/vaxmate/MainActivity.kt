package it.uninsubria.vaxmate

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.uninsubria.vaxmate.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private var listaVacciniDalDB: List<Vaccino> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        scaricaVacciniDaFirebase()

        setupLanguageButton(binding.languageButton.btnLanguage)

        binding.imgLogo.setOnClickListener { view ->
            val popupMenu = android.widget.PopupMenu(this, view)
            popupMenu.menuInflater.inflate(
                R.menu.logo_menu,
                popupMenu.menu
            )

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_logout -> {
                        auth.signOut()
                        recreate()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

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

        binding.btnElabora.setOnClickListener {
            eseguiCalcolo()
        }
    }

    private fun scaricaVacciniDaFirebase() {
        db.collection("Vaccini").get()
            .addOnSuccessListener { result ->
                val listaTemp = mutableListOf<Vaccino>()
                for (document in result) {
                    val vaccino = document.toObject(Vaccino::class.java)
                    listaTemp.add(vaccino)
                }
                listaVacciniDalDB = listaTemp
            }
            .addOnFailureListener {
                Toast.makeText(this, "Errore di connessione al database", Toast.LENGTH_SHORT).show()
            }
    }

    private fun eseguiCalcolo() {
        if (listaVacciniDalDB.isEmpty()) {
            Toast.makeText(this, "Caricamento vaccini in corso, riprova...", Toast.LENGTH_SHORT).show()
            return
        }

        // Reset degli errori visivi
        binding.etEta.error = null
        binding.etTerapia.error = null
        binding.etCondizione.error = null

        val terapiaScritta = binding.etTerapia.text.toString()
        val etaStringa = binding.etEta.text.toString().trim()
        val patologiaScritta = binding.etCondizione.text.toString()

        var ciSonoErrori = false

        // Validazione tramite VacciniManager
        val manager = VacciniManager()
        val terapiaValidata = manager.normalizzaTerapia(terapiaScritta)
        val patologiaValidata = manager.normalizzaPatologia(patologiaScritta)

        if (etaStringa.isEmpty()) {
            binding.etEta.error = "Inserisci l'età"
            ciSonoErrori = true
        }

        if (terapiaValidata == null) {
            binding.etTerapia.error = "Terapia non riconosciuta"
            ciSonoErrori = true
        }

        if (patologiaValidata == null) {
            binding.etCondizione.error = "Patologia non riconosciuta"
            ciSonoErrori = true
        }

        if (ciSonoErrori) return

        val eta = etaStringa.toInt()

        // Usiamo !! perché a questo punto siamo certi che non siano null
        val risultato = manager.calcolaRaccomandazioni(eta, terapiaValidata!!, patologiaValidata!!, listaVacciniDalDB)

        binding.tvRaccomandati.text = seVuoto(risultato.raccomandati.joinToString("\n") { "- ${it.nome}" })
        binding.tvPossibili.text = seVuoto(risultato.possibili.joinToString("\n") { "- ${it.nome}" })
        binding.tvControindicati.text = seVuoto(risultato.controindicati.joinToString("\n") { "- ${it.nome}" })

        binding.infoCard.visibility = View.GONE
        binding.layoutRisultati.visibility = View.VISIBLE
    }

    private fun seVuoto(testo: String): String {
        return if (testo.isBlank()) "Nessuno" else testo
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
        binding.tvGreetingSubtitle.text = "Consulta le linee guida e calcola le raccomandazioni."
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