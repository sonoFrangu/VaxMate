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

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private var listaVacciniDalDB: List<Vaccino> = emptyList()
    private val dbTerapie = arrayOf("Anti-TNF", "Corticosteroidi alto dosaggio", "Chemioterapia")
    private val dbPatologie = arrayOf("Diabete", "Cardiopatia cronica", "Asma cronica", "Gravidanza", "Immunodeficienza severa")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        scaricaVacciniDaFirebase()

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
                Toast.makeText(this, getString(R.string.db_error), Toast.LENGTH_SHORT).show()
            }
    }

    private fun eseguiCalcolo() {
        if (listaVacciniDalDB.isEmpty()) {
            Toast.makeText(this, getString(R.string.loading_vaccines), Toast.LENGTH_SHORT).show()
            return
        }

        binding.etEta.error = null
        binding.etTerapia.error = null
        binding.etCondizione.error = null

        val etaStringa = binding.etEta.text.toString().trim()
        val terapiaScritta = binding.etTerapia.text.toString().trim()
        val patologiaScritta = binding.etCondizione.text.toString().trim()

        var ciSonoErrori = false

        if (etaStringa.isEmpty()) {
            binding.etEta.error = getString(R.string.error_age)
            ciSonoErrori = true
        }

        val terapieLocalizzate = resources.getStringArray(R.array.terapie_array)
        val patologieLocalizzate = resources.getStringArray(R.array.patologie_array)

        // Se il campo è vuoto forziamo l'indice 0 ("Nessuna"), altrimenti lo cerchiamo nell'array
        val idxTerapia = if (terapiaScritta.isEmpty()) 0 else terapieLocalizzate.indexOfFirst { it.equals(terapiaScritta, ignoreCase = true) }
        val idxPatologia = if (patologiaScritta.isEmpty()) 0 else patologieLocalizzate.indexOfFirst { it.equals(patologiaScritta, ignoreCase = true) }

        if (idxTerapia == -1) {
            binding.etTerapia.error = getString(R.string.error_invalid_therapy)
            ciSonoErrori = true
        }

        if (idxPatologia == -1) {
            binding.etCondizione.error = getString(R.string.error_invalid_condition)
            ciSonoErrori = true
        }

        if (ciSonoErrori) return

        val terapiaDB = dbTerapie[idxTerapia]
        val patologiaDB = dbPatologie[idxPatologia]
        val eta = etaStringa.toInt()

        val manager = VacciniManager()
        val risultato = manager.calcolaRaccomandazioni(eta, terapiaDB, patologiaDB, listaVacciniDalDB)

        binding.tvRaccomandati.text = seVuoto(risultato.raccomandati.joinToString("\n") { "- ${it.nome}" })
        binding.tvPossibili.text = seVuoto(risultato.possibili.joinToString("\n") { "- ${it.nome}" })
        binding.tvControindicati.text = seVuoto(risultato.controindicati.joinToString("\n") { "- ${it.nome}" })

        binding.infoCard.visibility = View.GONE
        binding.layoutRisultati.visibility = View.VISIBLE
    }

    private fun seVuoto(testo: String): String {
        return testo.ifBlank { getString(R.string.empty_result) }
    }

    private fun impostaSalutoOspite() {
        val salutoBase = getString(R.string.welcome_base)
        val ruolo = getString(R.string.guest_role)
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
        binding.tvGreetingSubtitle.text = getString(R.string.guest_subtitle)
        mostraContenuto()
    }

    private fun impostaSalutoMedico(cognome: String) {
        val salutoBase = getString(R.string.greeting_base)
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
        binding.tvGreetingSubtitle.text = getString(R.string.doctor_subtitle)
        mostraContenuto()
    }

    private fun mostraContenuto() {
        binding.progressBar.visibility = View.GONE
        binding.mainContent.visibility = View.VISIBLE
    }
}