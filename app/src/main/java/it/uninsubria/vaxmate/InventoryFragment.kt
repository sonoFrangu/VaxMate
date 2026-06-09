package it.uninsubria.vaxmate

import android.content.Context
import android.content.Intent
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

class InventoryFragment : Fragment(R.layout.fragment_inventory) {

    private lateinit var adapter: VaccinoAdapter
    private val dbManager = DatabaseManager()
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

        adapter = VaccinoAdapter(emptyList())
        rvVaccini.layoutManager = LinearLayoutManager(requireContext())
        rvVaccini.adapter = adapter

        val utenteCorrente = auth.currentUser

        if (utenteCorrente == null) {
            // L'UTENTE È UN OSPITE
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

            btnLogin.setOnClickListener {
                startActivity(Intent(requireContext(), DoctorLoginActivity::class.java))
            }
            btnRegister.setOnClickListener {
                startActivity(Intent(requireContext(), RegisterActivity::class.java))
            }

        } else {
            // L'UTENTE È UN MEDICO REGISTRATO
            layoutGuest.visibility = View.GONE
            progressBar.visibility = View.VISIBLE

            db.collection("Medici").document(utenteCorrente.uid).get()
                .addOnSuccessListener { document ->
                    val nomeOspedale = document.getString("ospedale") ?: "Ospedale Sconosciuto"

                    dbManager.getVaccini(
                        onSuccess = { lista ->
                            progressBar.visibility = View.GONE
                            tvHospitalInfo.text = "Sede: $nomeOspedale\nVaccini a catalogo: ${lista.size}"
                            tvHospitalInfo.visibility = View.VISIBLE

                            rvVaccini.visibility = View.VISIBLE
                            adapter.aggiornaDati(lista)
                        },
                        onFailure = {
                            progressBar.visibility = View.GONE
                            Toast.makeText(requireContext(), "Errore di caricamento", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                .addOnFailureListener {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Errore recupero dati medico", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}