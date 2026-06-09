package it.uninsubria.vaxmate

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class ItemInventario(val nome: String, val tipo: String, val quantita: Int)

class InventarioAdapter(private var lista: List<ItemInventario>) :
    RecyclerView.Adapter<InventarioAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNome: TextView = view.findViewById(R.id.tvNomeVaccino)
        val tvTipo: TextView = view.findViewById(R.id.tvTipoVaccino)
        val tvQuantita: TextView = view.findViewById(R.id.tvQuantitaVaccino)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_vaccino, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        holder.tvNome.text = item.nome
        holder.tvTipo.text = "Tipo: ${item.tipo.replace("_", " ").replaceFirstChar { it.uppercase() }}"
        holder.tvQuantita.text = "${item.quantita} dosi"
    }

    override fun getItemCount() = lista.size

    fun aggiornaDati(nuovaLista: List<ItemInventario>) {
        lista = nuovaLista
        notifyDataSetChanged()
    }
}
class InventoryFragment : Fragment(R.layout.fragment_inventory) {

    private lateinit var adapter: InventarioAdapter
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvVaccini = view.findViewById<RecyclerView>(R.id.rvVaccini)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBarInventory)
        val layoutGuest = view.findViewById<LinearLayout>(R.id.layoutGuest)
        val tvHospitalInfo = view.findViewById<TextView>(R.id.tvHospitalInfo)

        val btnLogin = view.findViewById<MaterialButton>(R.id.btnGuestLogin)
        val btnRegister = view.findViewById<MaterialButton>(R.id.btnGuestRegister)

        adapter = InventarioAdapter(emptyList())
        rvVaccini.layoutManager = LinearLayoutManager(requireContext())
        rvVaccini.adapter = adapter

        val utenteCorrente = auth.currentUser

        if (utenteCorrente == null) {
            layoutGuest.visibility = View.VISIBLE
            rvVaccini.visibility = View.GONE
            progressBar.visibility = View.GONE
            tvHospitalInfo.visibility = View.GONE

            val reteDisponibile = isNetworkAvailable()
            btnLogin.isEnabled = reteDisponibile
            btnRegister.isEnabled = reteDisponibile

            if (!reteDisponibile) {
                btnLogin.setBackgroundColor(android.graphics.Color.GRAY)
                btnRegister.setTextColor(android.graphics.Color.GRAY)
                btnRegister.strokeColor = android.content.res.ColorStateList.valueOf(android.graphics.Color.GRAY)
                btnRegister.iconTint = android.content.res.ColorStateList.valueOf(android.graphics.Color.GRAY)
            }

            btnLogin.setOnClickListener { startActivity(Intent(requireContext(), DoctorLoginActivity::class.java)) }
            btnRegister.setOnClickListener { startActivity(Intent(requireContext(), RegisterActivity::class.java)) }

        } else {
            // --- MODALITÀ MEDICO REGISTRATO ---
            layoutGuest.visibility = View.GONE
            progressBar.visibility = View.VISIBLE

            db.collection("Medici").document(utenteCorrente.uid).get()
                .addOnSuccessListener { document ->
                    val nomeOspedaleBello = document.getString("ospedale") ?: ""

                    val idDocumentoInventario = convertiNomeOspedaleInId(nomeOspedaleBello)

                    db.collection("Inventari").document(idDocumentoInventario).get()
                        .addOnSuccessListener { invDoc ->
                            val lotti = invDoc.get("lotti") as? List<Map<String, Any>> ?: emptyList()

                            val mappaQuantita = mutableMapOf<String, Int>()
                            for (lotto in lotti) {
                                val idVac = lotto["id_vaccino"] as? String ?: continue
                                val qta = (lotto["quantita"] as? Number)?.toInt() ?: 0
                                mappaQuantita[idVac] = mappaQuantita.getOrDefault(idVac, 0) + qta
                            }

                            db.collection("Vaccini").get().addOnSuccessListener { vacciniResult ->
                                val listaFinale = mutableListOf<ItemInventario>()

                                for (vacDoc in vacciniResult) {
                                    val id = vacDoc.id
                                    val nome = vacDoc.getString("nome") ?: id
                                    val tipo = vacDoc.getString("tipo") ?: ""

                                    val quantita = mappaQuantita[id] ?: 0
                                    listaFinale.add(ItemInventario(nome, tipo, quantita))
                                }

                                progressBar.visibility = View.GONE
                                tvHospitalInfo.text = "Sede: $nomeOspedaleBello\nTotale dosi a magazzino: ${listaFinale.sumOf { it.quantita }}"
                                tvHospitalInfo.visibility = View.VISIBLE

                                rvVaccini.visibility = View.VISIBLE
                                adapter.aggiornaDati(listaFinale.sortedByDescending { it.quantita })

                            }.addOnFailureListener {
                                progressBar.visibility = View.GONE
                                Toast.makeText(requireContext(), "Errore catalogo vaccini", Toast.LENGTH_SHORT).show()
                            }

                        }.addOnFailureListener {
                            progressBar.visibility = View.GONE
                            Toast.makeText(requireContext(), "Inventario non trovato nel database", Toast.LENGTH_SHORT).show()
                        }
                }.addOnFailureListener {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Errore dati medico", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun convertiNomeOspedaleInId(nomeOspedale: String): String {
        return when (nomeOspedale) {
            "Ospedale di Circolo Varese" -> "ospedale_circolo"
            "Ospedale Del Ponte" -> "ospedale_delponte"
            "Ospedale di Como" -> "ospedale_como"
            else -> nomeOspedale
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}