package it.uninsubria.vaxmate

class VacciniManager {

    fun normalizzaTerapia(input: String): String? {
        if (input.isBlank()) return "Nessuna"

        return when (input.trim().lowercase()) {
            "nessuna", "none" -> "Nessuna"
            "anti-tnf", "antitnf" -> "Anti-TNF"
            "corticosteroidi alto dosaggio", "high-dose corticosteroids", "corticosteroidi" -> "Corticosteroidi alto dosaggio"
            "chemioterapia", "chemotherapy" -> "Chemioterapia"
            else -> null // La parola non esiste
        }
    }

    fun normalizzaPatologia(input: String): String? {
        if (input.isBlank()) return "Nessuna"

        return when (input.trim().lowercase()) {
            "nessuna", "none" -> "Nessuna"
            "diabete", "diabetes" -> "Diabete"
            "cardiopatia cronica", "chronic heart disease", "cardiopatia" -> "Cardiopatia cronica"
            "asma cronica", "chronic asthma", "asma" -> "Asma cronica"
            "gravidanza", "pregnancy" -> "Gravidanza"
            "immunodeficienza severa", "severe immunodeficiency", "immunodeficienza" -> "Immunodeficienza severa"
            else -> null // La parola non esiste
        }
    }

    fun calcolaRaccomandazioni(
        etaPaziente: Int,
        terapiaNormalizzata: String,
        patologiaNormalizzata: String,
        listaCompleta: List<Vaccino>
    ): RisultatoVaccini {

        val raccomandati = mutableListOf<Vaccino>()
        val possibili = mutableListOf<Vaccino>()
        val controindicati = mutableListOf<Vaccino>()

        for (vaccino in listaCompleta) {

            val haControindicazioneTerapia = vaccino.terapie_controindicate.any {
                it.equals(terapiaNormalizzata, ignoreCase = true)
            }
            val haControindicazioneCondizione = vaccino.condizioni_controindicate.any {
                it.equals(patologiaNormalizzata, ignoreCase = true)
            }
            val eRaccomandatoCondizione = vaccino.condizioni_raccomandate.any {
                it.equals(patologiaNormalizzata, ignoreCase = true)
            }

            if (haControindicazioneTerapia || haControindicazioneCondizione) {
                controindicati.add(vaccino)
            } else if (eRaccomandatoCondizione) {
                raccomandati.add(vaccino)
            } else if (etaPaziente in vaccino.eta_minima..vaccino.eta_massima) {
                possibili.add(vaccino)
            }
        }

        return RisultatoVaccini(raccomandati, possibili, controindicati)
    }
}