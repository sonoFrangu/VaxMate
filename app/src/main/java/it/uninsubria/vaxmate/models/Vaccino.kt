package it.uninsubria.vaxmate.models

import java.util.Locale

data class Vaccino(
    var nome_it: String = "",
    var nome_en: String = "",
    var tipo_it: String = "",
    var tipo_en: String = "",
    var eta_minima: Int = 0,
    var eta_massima: Int = 130,
    var condizioni_raccomandate: List<String> = emptyList(),
    var terapie_controindicate: List<String> = emptyList(),
    var condizioni_controindicate: List<String> = emptyList()
) {
    fun getNomeLocalizzato(): String {
        return if (Locale.getDefault().language == "en") nome_en else nome_it
    }

    fun getTipoLocalizzato(): String {
        return if (Locale.getDefault().language == "en") tipo_en else tipo_it
    }
}