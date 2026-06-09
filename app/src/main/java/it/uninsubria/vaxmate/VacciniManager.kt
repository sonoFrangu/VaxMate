package it.uninsubria.vaxmate

class VacciniManager {

    fun calcolaRaccomandazioni(
        etaPaziente: Int,
        terapiaPaziente: String,
        patologiaPaziente: String,
        listaCompleta: List<Vaccino>
    ): RisultatoVaccini {

        val raccomandati = mutableListOf<Vaccino>()
        val possibili = mutableListOf<Vaccino>()
        val controindicati = mutableListOf<Vaccino>()

        for (vaccino in listaCompleta) {

            if (vaccino.terapie_controindicate.contains(terapiaPaziente) ||
                vaccino.condizioni_controindicate.contains(patologiaPaziente)
            ) {
                controindicati.add(vaccino)
            } else if (vaccino.condizioni_raccomandate.contains(patologiaPaziente)) {
                raccomandati.add(vaccino)
            } else if (etaPaziente in vaccino.eta_minima..vaccino.eta_massima) {
                possibili.add(vaccino)
            }
        }

        return RisultatoVaccini(raccomandati, possibili, controindicati)
    }
}

