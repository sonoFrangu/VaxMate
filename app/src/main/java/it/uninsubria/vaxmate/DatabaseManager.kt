package it.uninsubria.vaxmate

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DatabaseManager {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun creaAccountMedico(email: String, pass: String, nome: String, cognome: String, ospedale: String, callback: (Boolean) -> Unit) {
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid ?: ""
                salvaDatiFirestore(userId, nome, cognome, email, ospedale, callback)
            } else {
                callback(false)
            }
        }
    }

    fun loginMedico(email: String, pass: String, callback: (Boolean) -> Unit) {
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            callback(task.isSuccessful)
        }
    }

    private fun salvaDatiFirestore(uid: String, nome: String, cognome: String, email: String, ospedale: String, callback: (Boolean) -> Unit) {
        val datiMedico = hashMapOf(
            "nome" to nome,
            "cognome" to cognome,
            "email" to email,
            "ospedale" to ospedale
        )

        db.collection("Medici").document(uid).set(datiMedico).addOnCompleteListener { task ->
            callback(task.isSuccessful)
        }
    }

    fun getVaccini(onSuccess: (List<Vaccino>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("Vaccini").get()
            .addOnSuccessListener { result ->
                val listaTemp = mutableListOf<Vaccino>()
                for (document in result) {
                    val vaccino = document.toObject(Vaccino::class.java)
                    listaTemp.add(vaccino)
                }
                onSuccess(listaTemp)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun getDatiMedico(uid: String, onSuccess: (Map<String, Any>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("Medici").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    onSuccess(document.data ?: emptyMap())
                } else {
                    onFailure(Exception("Documento non trovato"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    /*fun popolaDatabaseVaccini(callback: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val vacciniRef = db.collection("Vaccini")

        val listaVaccini = listOf(
            mapOf(
                "id" to "esavalente",
                "nome_it" to "Esavalente (DTPa-Polio-EpB-Hib)",
                "nome_en" to "Hexavalent (DTPa-Polio-EpB-Hib)",
                "tipo_it" to "inattivato",
                "tipo_en" to "inactivated",
                "eta_minima" to 0,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf<String>(),
                "terapie_controindicate" to listOf<String>(),
                "condizioni_controindicate" to listOf<String>()
            ),
            mapOf(
                "id" to "rotavirus",
                "nome_it" to "Rotavirus",
                "nome_en" to "Rotavirus",
                "tipo_it" to "vivo_attenuato",
                "tipo_en" to "live_attenuated",
                "eta_minima" to 0,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf<String>(),
                "terapie_controindicate" to listOf("Anti-TNF", "Corticosteroidi alto dosaggio", "Chemioterapia"),
                "condizioni_controindicate" to listOf("Immunodeficienza severa", "Gravidanza")
            ),
            mapOf(
                "id" to "pcv20",
                "nome_it" to "Pneumococco Coniugato (PCV20)",
                "nome_en" to "Pneumococcal Conjugate (PCV20)",
                "tipo_it" to "inattivato",
                "tipo_en" to "inactivated",
                "eta_minima" to 0,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf("Diabete", "Cardiopatia cronica", "Asma cronica", "Immunodeficienza severa"),
                "terapie_controindicate" to listOf<String>(),
                "condizioni_controindicate" to listOf<String>()
            ),
            mapOf(
                "id" to "meningococco_b",
                "nome_it" to "Meningococco B",
                "nome_en" to "Meningococcal B",
                "tipo_it" to "inattivato",
                "tipo_en" to "inactivated",
                "eta_minima" to 0,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf("Immunodeficienza severa"),
                "terapie_controindicate" to listOf<String>(),
                "condizioni_controindicate" to listOf<String>()
            ),
            mapOf(
                "id" to "meningococco_acwy",
                "nome_it" to "Meningococco ACWY",
                "nome_en" to "Meningococcal ACWY",
                "tipo_it" to "inattivato",
                "tipo_en" to "inactivated",
                "eta_minima" to 0,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf("Immunodeficienza severa"),
                "terapie_controindicate" to listOf<String>(),
                "condizioni_controindicate" to listOf<String>()
            ),
            mapOf(
                "id" to "mprv",
                "nome_it" to "MPRV (Morbillo-Parotite-Rosolia-Varicella)",
                "nome_en" to "MMRV (Measles-Mumps-Rubella-Varicella)",
                "tipo_it" to "vivo_attenuato",
                "tipo_en" to "live_attenuated",
                "eta_minima" to 1,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf<String>(),
                "terapie_controindicate" to listOf("Anti-TNF", "Corticosteroidi alto dosaggio", "Chemioterapia"),
                "condizioni_controindicate" to listOf("Immunodeficienza severa", "Gravidanza")
            ),
            mapOf(
                "id" to "hpv",
                "nome_it" to "HPV (Anti-Papillomavirus)",
                "nome_en" to "HPV (Human Papillomavirus)",
                "tipo_it" to "ricombinante",
                "tipo_en" to "recombinant",
                "eta_minima" to 9,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf("Immunodeficienza severa"),
                "terapie_controindicate" to listOf<String>(),
                "condizioni_controindicate" to listOf("Gravidanza")
            ),
            mapOf(
                "id" to "dtpa_polio",
                "nome_it" to "dTpa-Polio (Richiamo)",
                "nome_en" to "Tdap-IPV (Booster)",
                "tipo_it" to "inattivato",
                "tipo_en" to "inactivated",
                "eta_minima" to 4,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf("Gravidanza"),
                "terapie_controindicate" to listOf<String>(),
                "condizioni_controindicate" to listOf<String>()
            ),
            mapOf(
                "id" to "varicella",
                "nome_it" to "Varicella",
                "nome_en" to "Varicella (Chickenpox)",
                "tipo_it" to "vivo_attenuato",
                "tipo_en" to "live_attenuated",
                "eta_minima" to 1,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf<String>(),
                "terapie_controindicate" to listOf("Anti-TNF", "Corticosteroidi alto dosaggio", "Chemioterapia"),
                "condizioni_controindicate" to listOf("Immunodeficienza severa", "Gravidanza")
            ),
            mapOf(
                "id" to "shingrix",
                "nome_it" to "Herpes Zoster (Shingrix)",
                "nome_en" to "Herpes Zoster (Shingrix)",
                "tipo_it" to "ricombinante",
                "tipo_en" to "recombinant",
                "eta_minima" to 18,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf("Diabete", "Cardiopatia cronica", "Immunodeficienza severa"),
                "terapie_controindicate" to listOf<String>(),
                "condizioni_controindicate" to listOf<String>()
            ),
            mapOf(
                "id" to "antinfluenzale_inattivato",
                "nome_it" to "Antinfluenzale Inattivato",
                "nome_en" to "Inactivated Influenza",
                "tipo_it" to "inattivato",
                "tipo_en" to "inactivated",
                "eta_minima" to 0,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf("Diabete", "Cardiopatia cronica", "Asma cronica", "Gravidanza", "Immunodeficienza severa"),
                "terapie_controindicate" to listOf<String>(),
                "condizioni_controindicate" to listOf<String>()
            ),
            mapOf(
                "id" to "antinfluenzale_laiv",
                "nome_it" to "Antinfluenzale LAIV",
                "nome_en" to "Influenza LAIV",
                "tipo_it" to "vivo_attenuato",
                "tipo_en" to "live_attenuated",
                "eta_minima" to 2,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf<String>(),
                "terapie_controindicate" to listOf("Anti-TNF", "Corticosteroidi alto dosaggio", "Chemioterapia"),
                "condizioni_controindicate" to listOf("Immunodeficienza severa", "Gravidanza", "Asma cronica")
            ),
            mapOf(
                "id" to "febbre_gialla",
                "nome_it" to "Febbre Gialla",
                "nome_en" to "Yellow Fever",
                "tipo_it" to "vivo_attenuato",
                "tipo_en" to "live_attenuated",
                "eta_minima" to 0,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf<String>(),
                "terapie_controindicate" to listOf("Anti-TNF", "Corticosteroidi alto dosaggio", "Chemioterapia"),
                "condizioni_controindicate" to listOf("Immunodeficienza severa", "Gravidanza")
            ),
            mapOf(
                "id" to "epatite_a",
                "nome_it" to "Epatite A",
                "nome_en" to "Hepatitis A",
                "tipo_it" to "inattivato",
                "tipo_en" to "inactivated",
                "eta_minima" to 1,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf<String>(),
                "terapie_controindicate" to listOf<String>(),
                "condizioni_controindicate" to listOf<String>()
            ),
            mapOf(
                "id" to "epatite_b",
                "nome_it" to "Epatite B",
                "nome_en" to "Hepatitis B",
                "tipo_it" to "inattivato",
                "tipo_en" to "inactivated",
                "eta_minima" to 0,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf("Diabete", "Immunodeficienza severa"),
                "terapie_controindicate" to listOf<String>(),
                "condizioni_controindicate" to listOf<String>()
            ),
            mapOf(
                "id" to "covid19",
                "nome_it" to "Covid-19 (mRNA)",
                "nome_en" to "Covid-19 (mRNA)",
                "tipo_it" to "mrna",
                "tipo_en" to "mrna",
                "eta_minima" to 0,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf("Diabete", "Cardiopatia cronica", "Asma cronica", "Gravidanza", "Immunodeficienza severa"),
                "terapie_controindicate" to listOf<String>(),
                "condizioni_controindicate" to listOf<String>()
            ),
            mapOf(
                "id" to "ppsv23",
                "nome_it" to "Pneumococco Polisaccaridico (PPSV23)",
                "nome_en" to "Pneumococcal Polysaccharide (PPSV23)",
                "tipo_it" to "inattivato",
                "tipo_en" to "inactivated",
                "eta_minima" to 2,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf("Diabete", "Cardiopatia cronica", "Asma cronica", "Immunodeficienza severa"),
                "terapie_controindicate" to listOf<String>(),
                "condizioni_controindicate" to listOf<String>()
            ),
            mapOf(
                "id" to "rabbia",
                "nome_it" to "Antirabico (Rabbia)",
                "nome_en" to "Rabies",
                "tipo_it" to "inattivato",
                "tipo_en" to "inactivated",
                "eta_minima" to 0,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf<String>(),
                "terapie_controindicate" to listOf<String>(),
                "condizioni_controindicate" to listOf<String>()
            ),
            mapOf(
                "id" to "febbre_tifoide_vivo",
                "nome_it" to "Febbre Tifoide (Orale)",
                "nome_en" to "Typhoid Fever (Oral)",
                "tipo_it" to "vivo_attenuato",
                "tipo_en" to "live_attenuated",
                "eta_minima" to 5,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf<String>(),
                "terapie_controindicate" to listOf("Anti-TNF", "Corticosteroidi alto dosaggio", "Chemioterapia"),
                "condizioni_controindicate" to listOf("Immunodeficienza severa", "Gravidanza")
            ),
            mapOf(
                "id" to "dengue",
                "nome_it" to "Dengue (Qdenga)",
                "nome_en" to "Dengue (Qdenga)",
                "tipo_it" to "vivo_attenuato",
                "tipo_en" to "live_attenuated",
                "eta_minima" to 4,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf<String>(),
                "terapie_controindicate" to listOf("Anti-TNF", "Corticosteroidi alto dosaggio", "Chemioterapia"),
                "condizioni_controindicate" to listOf("Immunodeficienza severa", "Gravidanza")
            ),
            mapOf(
                "id" to "encefalite_zecca",
                "nome_it" to "Encefalite da Zecca (TBE)",
                "nome_en" to "Tick-Borne Encephalitis (TBE)",
                "tipo_it" to "inattivato",
                "tipo_en" to "inactivated",
                "eta_minima" to 1,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf<String>(),
                "terapie_controindicate" to listOf<String>(),
                "condizioni_controindicate" to listOf<String>()
            )
        )

        var completati = 0
        var errore = false

        for (vaccino in listaVaccini) {
            val idDocumento = vaccino["id"] as String
            val datiSenzaId = vaccino.filterKeys { it != "id" }

            vacciniRef.document(idDocumento).set(datiSenzaId)
                .addOnSuccessListener {
                    completati++
                    if (completati == listaVaccini.size && !errore) {
                        callback(true)
                    }
                }
                .addOnFailureListener {
                    errore = true
                    callback(false)
                }
        }
    }*/
}