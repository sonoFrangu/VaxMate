package it.uninsubria.vaxmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.play.integrity.internal.t

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
        val context = holder.itemView.context

        holder.tvNome.text = item.nome

        val tipoTesto = if (item.tipo.isNotEmpty()) {
            item.tipo
                .replace("_", " ")
                .replaceFirstChar { it.uppercase() }
        } else {
            context.getString(R.string.not_specified)
        }
        holder.tvTipo.text = context.getString(R.string.vaccine_type_placeholder, tipoTesto)
        holder.tvQuantita.text = context.getString(R.string.doses_format, item.quantita)

    }

    override fun getItemCount() = lista.size

    fun aggiornaDati(nuovaLista: List<ItemInventario>) {
        lista = nuovaLista
        notifyDataSetChanged()
    }
}