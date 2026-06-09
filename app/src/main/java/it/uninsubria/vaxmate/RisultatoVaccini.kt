package it.uninsubria.vaxmate

data class RisultatoVaccini(
    val raccomandati: List<Vaccino>,
    val possibili: List<Vaccino>,
    val controindicati: List<Vaccino>
)