package it.uninsubria.vaxmate

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import it.uninsubria.vaxmate.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var listaVacciniDalDB: List<Vaccino> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scaricaVacciniDaFirebase()

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
                Toast.makeText(requireContext(), getString(R.string.db_error), Toast.LENGTH_SHORT).show()
            }
    }

    private fun eseguiCalcolo() {
        if (listaVacciniDalDB.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.loading_vaccines), Toast.LENGTH_SHORT).show()
            return
        }

        binding.etEta.error = null
        binding.etTerapia.error = null
        binding.etCondizione.error = null

        val terapiaScritta = binding.etTerapia.text.toString()
        val etaStringa = binding.etEta.text.toString().trim()
        val patologiaScritta = binding.etCondizione.text.toString()

        var ciSonoErrori = false

        val manager = VacciniManager()
        val terapiaValidata = manager.normalizzaTerapia(terapiaScritta)
        val patologiaValidata = manager.normalizzaPatologia(patologiaScritta)

        if (etaStringa.isEmpty()) {
            binding.etEta.error = getString(R.string.error_age)
            ciSonoErrori = true
        }

        if (terapiaValidata == null) {
            binding.etTerapia.error = getString(R.string.error_invalid_therapy)
            ciSonoErrori = true
        }

        if (patologiaValidata == null) {
            binding.etCondizione.error = getString(R.string.error_invalid_condition)
            ciSonoErrori = true
        }

        if (ciSonoErrori) return

        val eta = etaStringa.toInt()
        val risultato = manager.calcolaRaccomandazioni(eta, terapiaValidata!!, patologiaValidata!!, listaVacciniDalDB)

        binding.layoutListaVaccini.removeAllViews()

        aggiungiCardVaccini(risultato.raccomandati, "Raccomandato", R.color.bg_raccomandati, R.color.text_raccomandati, R.drawable.ic_check)
        aggiungiCardVaccini(risultato.possibili, "Possibile", R.color.bg_possibili, R.color.text_possibili, R.drawable.ic_info)
        aggiungiCardVaccini(risultato.controindicati, "Controindicato", R.color.bg_controindicati, R.color.text_controindicati, R.drawable.ic_cross)

        binding.infoCard.visibility = View.GONE
        binding.layoutRisultati.visibility = View.VISIBLE
    }

    private fun aggiungiCardVaccini(
        vaccini: List<Vaccino>,
        testoStato: String,
        colorBgRes: Int,
        colorTextRes: Int,
        iconRes: Int
    ) {
        val inflater = LayoutInflater.from(requireContext())

        for (vaccino in vaccini) {
            val cardView = inflater.inflate(R.layout.item_vaccino_card, binding.layoutListaVaccini, false)

            val container = cardView.findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardContainer)
            val ivIcona = cardView.findViewById<android.widget.ImageView>(R.id.ivIconaStato)
            val tvNome = cardView.findViewById<android.widget.TextView>(R.id.tvNomeVaccino)
            val tvStato = cardView.findViewById<android.widget.TextView>(R.id.tvStatoVaccino)
            val tvTipo = cardView.findViewById<android.widget.TextView>(R.id.tvTipoVaccino)

            container.setCardBackgroundColor(ContextCompat.getColor(requireContext(), colorBgRes))
            ivIcona.setImageResource(iconRes)
            ivIcona.setColorFilter(ContextCompat.getColor(requireContext(), colorTextRes))

            tvNome.text = vaccino.nome
            tvStato.text = testoStato
            tvStato.setTextColor(ContextCompat.getColor(requireContext(), colorTextRes))

            val tipo = vaccino.tipo ?: "Non specificato"
            tvTipo.text = "Tipo: $tipo"

            binding.layoutListaVaccini.addView(cardView)
        }
    }

    private fun impostaSalutoOspite() {
        val salutoBase = getString(R.string.welcome_base)
        val ruolo = getString(R.string.guest_role)
        val testoCompleto = salutoBase + " " + ruolo

        val spannableString = SpannableString(testoCompleto)
        val colorBlu = ContextCompat.getColor(requireContext(), R.color.primary)

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
        val salutoBase = getString(R.string.welcome_base)
        val nomeDottore = "Dr. $cognome"
        val testoCompleto = salutoBase + " " + nomeDottore

        val spannableString = SpannableString(testoCompleto)
        val colorBlu = ContextCompat.getColor(requireContext(), R.color.primary)

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}