package it.uninsubria.vaxmate

//Classe stampino per caricare Json da Firebase
data class Vaccino(
    var id: String = "",
    var nome: String = "",
    var tipo: String = "",
    var eta_minima: Int = 0,
    var eta_massima: Int = 130,
    var condizioni_raccomandate: List<String> = emptyList(),
    var terapie_controindicate: List<String> = emptyList(),
    var condizioni_controindicate: List<String> = emptyList()
)