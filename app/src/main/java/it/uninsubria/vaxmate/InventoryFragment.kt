package it.uninsubria.vaxmate

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class InventoryFragment : Fragment(R.layout.fragment_inventory) {

    private lateinit var adapter: VaccinoAdapter
    private val dbManager = DatabaseManager()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvVaccini = view.findViewById<RecyclerView>(R.id.rvVaccini)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBarInventory)

        adapter = VaccinoAdapter(emptyList())
        rvVaccini.layoutManager = LinearLayoutManager(requireContext())
        rvVaccini.adapter = adapter

        progressBar.visibility = View.VISIBLE
        rvVaccini.visibility = View.GONE

        dbManager.getVaccini(
            onSuccess = { lista ->
                progressBar.visibility = View.GONE
                rvVaccini.visibility = View.VISIBLE
                adapter.aggiornaDati(lista)
            },
            onFailure = {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Errore di caricamento", Toast.LENGTH_SHORT).show()
            }
        )
    }
}