package it.uninsubria.vaxmate

//Classe stampino per caricare Json da Firebase
data class Vaccino(
    val id: String = "",
    val nome: String = "",
    val tipo: String = "",
    val eta_minima: Int = 0,
    val eta_massima: Int = 130,
    val condizioni_raccomandate: List<String> = emptyList(),
    val terapie_controindicate: List<String> = emptyList(),
    val condizioni_controindicate: List<String> = emptyList()
)