package it.uninsubria.vaxmate.models

data class RisultatoVaccini(
    val raccomandati: List<Vaccino>,
    val possibili: List<Vaccino>,
    val controindicati: List<Vaccino>
)