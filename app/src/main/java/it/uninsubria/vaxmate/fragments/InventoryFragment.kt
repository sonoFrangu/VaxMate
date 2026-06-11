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
import it.uninsubria.vaxmate.R
import it.uninsubria.vaxmate.activities.DoctorLoginActivity
import it.uninsubria.vaxmate.activities.RegisterActivity
import it.uninsubria.vaxmate.adapters.InventarioAdapter
import it.uninsubria.vaxmate.utils.DatabaseManager

class InventoryFragment : Fragment(R.layout.fragment_inventory) {

    private lateinit var adapter: InventarioAdapter
    private val auth = FirebaseAuth.getInstance()
    private val databaseManager = DatabaseManager()

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

            btnLogin.setOnClickListener {
                startActivity(Intent(requireContext(), DoctorLoginActivity::class.java))
            }
            btnRegister.setOnClickListener {
                startActivity(Intent(requireContext(), RegisterActivity::class.java))
            }

        } else {
            // --- MODALITÀ MEDICO REGISTRATO ---
            layoutGuest.visibility = View.GONE
            progressBar.visibility = View.VISIBLE

            databaseManager.scaricaInventarioMedico(
                userId = utenteCorrente.uid,
                onSuccess = { nomeOspedale, listaInventario ->
                    progressBar.visibility = View.GONE

                    val totaleDosi = listaInventario.sumOf { it.quantita }
                    tvHospitalInfo.text = getString(R.string.hospital_inventory_info, nomeOspedale, totaleDosi)
                    tvHospitalInfo.visibility = View.VISIBLE

                    rvVaccini.visibility = View.VISIBLE
                    adapter.aggiornaDati(listaInventario.sortedByDescending { it.quantita })
                },
                onFailure = { codiceErrore ->
                    progressBar.visibility = View.GONE

                    val messaggioErrore = when(codiceErrore) {
                        "error_doctor_data" -> getString(R.string.error_doctor_data)
                        "error_inventory_not_found" -> getString(R.string.error_inventory_not_found)
                        "error_vaccine_catalog" -> getString(R.string.error_vaccine_catalog)
                        else -> getString(R.string.db_error)
                    }
                    Toast.makeText(requireContext(), messaggioErrore, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}