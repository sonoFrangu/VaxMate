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
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import it.uninsubria.vaxmate.utils.DatabaseManager
import it.uninsubria.vaxmate.R
import it.uninsubria.vaxmate.activities.DoctorLoginActivity
import it.uninsubria.vaxmate.activities.LoginActivity
import it.uninsubria.vaxmate.activities.RegisterActivity

class AccountFragment : Fragment(R.layout.fragment_account) {

    private val auth = FirebaseAuth.getInstance()
    private val dbManager = DatabaseManager()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Grafica Medico
        val scrollProfiloMedico = view.findViewById<ScrollView>(R.id.scrollProfiloMedico)
        val tvHeaderNome = view.findViewById<TextView>(R.id.tvHeaderNome)
        val tvProfiloNomeCognome = view.findViewById<TextView>(R.id.tvProfiloNomeCognome)
        val tvProfiloEmail = view.findViewById<TextView>(R.id.tvProfiloEmail)
        val tvProfiloOspedale = view.findViewById<TextView>(R.id.tvProfiloOspedale)
        val btnLogout = view.findViewById<MaterialButton>(R.id.btnAccountLogout)

        val progressBar = view.findViewById<ProgressBar>(R.id.progressBarAccount)

        // Grafica Ospite
        val layoutGuest = view.findViewById<LinearLayout>(R.id.layoutGuestAccount)
        val btnLogin = view.findViewById<MaterialButton>(R.id.btnGuestLogin)
        val btnRegister = view.findViewById<MaterialButton>(R.id.btnGuestRegister)

        val utenteCorrente = auth.currentUser

        if (utenteCorrente == null) {
            // --- MODALITÀ OSPITE ---
            layoutGuest.visibility = View.VISIBLE
            scrollProfiloMedico.visibility = View.GONE
            progressBar.visibility = View.GONE

            val reteDisponibile = isNetworkAvailable()
            btnLogin.isEnabled = reteDisponibile
            btnRegister.isEnabled = reteDisponibile

            if (!reteDisponibile) {
                btnLogin.setBackgroundColor(Color.GRAY)
                btnRegister.setTextColor(Color.GRAY)
                btnRegister.strokeColor = ColorStateList.valueOf(Color.GRAY)
                btnRegister.iconTint = ColorStateList.valueOf(Color.GRAY)
            }

            btnLogin.setOnClickListener { startActivity(Intent(requireContext(), DoctorLoginActivity::class.java)) }
            btnRegister.setOnClickListener { startActivity(Intent(requireContext(), RegisterActivity::class.java)) }

        } else {
            // --- MODALITÀ MEDICO LOGGATO ---
            layoutGuest.visibility = View.GONE
            progressBar.visibility = View.VISIBLE

            dbManager.getDatiMedico(
                uid = utenteCorrente.uid,
                onSuccess = { dati ->
                    progressBar.visibility = View.GONE

                    val nome = dati["nome"] as? String ?: ""
                    val cognome = dati["cognome"] as? String ?: ""
                    val email = dati["email"] as? String ?: ""
                    val ospedale = dati["ospedale"] as? String ?: ""

                    tvHeaderNome.text = "Dr. $nome $cognome"
                    tvProfiloNomeCognome.text = "$nome $cognome"
                    tvProfiloEmail.text = email
                    tvProfiloOspedale.text = ospedale

                    scrollProfiloMedico.visibility = View.VISIBLE
                },
                onFailure = {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Errore caricamento profilo", Toast.LENGTH_SHORT).show()
                }
            )

            btnLogout.setOnClickListener {
                auth.signOut()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().recreate()
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