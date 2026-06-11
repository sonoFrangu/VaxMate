package it.uninsubria.vaxmate.fragments

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
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
import it.uninsubria.vaxmate.adapters.InventarioAdapter
import it.uninsubria.vaxmate.R
import it.uninsubria.vaxmate.activities.DoctorLoginActivity
import it.uninsubria.vaxmate.activities.RegisterActivity
import it.uninsubria.vaxmate.models.ItemInventario
import java.util.Locale

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
                btnLogin.setBackgroundColor(Color.GRAY)
                btnRegister.setTextColor(Color.GRAY)
                btnRegister.strokeColor = ColorStateList.valueOf(Color.GRAY)
                btnRegister.iconTint = ColorStateList.valueOf(Color.GRAY)
            }

            btnLogin.setOnClickListener { startActivity(
                Intent(
                    requireContext(),
                    DoctorLoginActivity::class.java
                )
            ) }
            btnRegister.setOnClickListener { startActivity(
                Intent(
                    requireContext(),
                    RegisterActivity::class.java)
                )
            }

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

                                val lingua = Locale.getDefault().language
                                val campoNome = if (lingua == "en") "nome_en" else "nome_it"
                                val campoTipo = if (lingua == "en") "tipo_en" else "tipo_it"

                                for (vacDoc in vacciniResult) {
                                    val id = vacDoc.id

                                    val nome = vacDoc.getString(campoNome) ?: vacDoc.getString("nome_en") ?: id
                                    val tipo = vacDoc.getString(campoTipo) ?: vacDoc.getString("tipo_en") ?: ""

                                    val quantita = mappaQuantita[id] ?: 0
                                    listaFinale.add(ItemInventario(nome, tipo, quantita))
                                }

                                progressBar.visibility = View.GONE

                                // Localizziamo anche la stringa di recap dell'ospedale
                                val totaleDosi = listaFinale.sumOf { it.quantita }
                                tvHospitalInfo.text = getString(R.string.hospital_inventory_info, nomeOspedaleBello, totaleDosi)
                                tvHospitalInfo.visibility = View.VISIBLE

                                rvVaccini.visibility = View.VISIBLE
                                adapter.aggiornaDati(listaFinale.sortedByDescending { it.quantita })

                            }.addOnFailureListener {
                                progressBar.visibility = View.GONE
                                Toast.makeText(requireContext(), getString(R.string.error_vaccine_catalog), Toast.LENGTH_SHORT).show()
                            }

                        }.addOnFailureListener {
                            progressBar.visibility = View.GONE
                            Toast.makeText(requireContext(), getString(R.string.error_inventory_not_found), Toast.LENGTH_SHORT).show()
                        }
                }.addOnFailureListener {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), getString(R.string.error_doctor_data), Toast.LENGTH_SHORT).show()
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