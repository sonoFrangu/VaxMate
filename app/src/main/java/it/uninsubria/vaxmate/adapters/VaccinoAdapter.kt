package it.uninsubria.vaxmate.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.uninsubria.vaxmate.R
import it.uninsubria.vaxmate.models.Vaccino

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
        val context = holder.itemView.context

        holder.tvNome.text = vaccino.getNomeLocalizzato()

        val tipoGrezzo = vaccino.getTipoLocalizzato()
        val tipoFormattato = if (tipoGrezzo.isNotEmpty()) {
            tipoGrezzo
                .replace("_", " ")
                .replaceFirstChar { it.uppercase() }
        } else {
            context.getString(R.string.not_specified)
        }

        holder.tvTipo.text = context.getString(R.string.vaccine_type_placeholder, tipoFormattato)
    }

    override fun getItemCount() = listaVaccini.size

}