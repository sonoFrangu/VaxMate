package it.uninsubria.vaxmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VaccinoAdapter(private var listaVaccini: List<Vaccino>) :
    RecyclerView.Adapter<VaccinoAdapter.VaccinoViewHolder>() {

    class VaccinoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNome: TextView = view.findViewById(R.id.tvNomeVaccino)
        val tvTipo: TextView = view.findViewById(R.id.tvTipoVaccino)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VaccinoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vaccino, parent, false)
        return VaccinoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VaccinoViewHolder, position: Int) {
        val vaccino = listaVaccini[position]
        holder.tvNome.text = vaccino.nome
        holder.tvTipo.text = "Tipo: ${vaccino.tipo.replace("_", " ").capitalize()}"
    }

    override fun getItemCount() = listaVaccini.size

    fun aggiornaDati(nuovaLista: List<Vaccino>) {
        listaVaccini = nuovaLista
        notifyDataSetChanged()
    }
}