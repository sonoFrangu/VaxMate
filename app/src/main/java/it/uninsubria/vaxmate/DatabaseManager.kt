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
                "nome" to "Esavalente (DTPa-Polio-EpB-Hib)",
                "tipo" to "inattivato",
                "eta_minima" to 0,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf<String>(),
                "terapie_controindicate" to listOf<String>(),
                "condizioni_controindicate" to listOf<String>()
            ),
            mapOf(
                "id" to "rotavirus",
                "nome" to "Rotavirus",
                "tipo" to "vivo_attenuato",
                "eta_minima" to 0,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf<String>(),
                "terapie_controindicate" to listOf("Anti-TNF", "Corticosteroidi alto dosaggio", "Chemioterapia"),
                "condizioni_controindicate" to listOf("Immunodeficienza severa", "Gravidanza")
            ),
            mapOf(
                "id" to "pcv20",
                "nome" to "Pneumococco Coniugato (PCV20)",
                "tipo" to "inattivato",
                "eta_minima" to 0,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf("Diabete", "Cardiopatia cronica", "Asma cronica", "Immunodeficienza severa"),
                "terapie_controindicate" to listOf<String>(),
                "condizioni_controindicate" to listOf<String>()
            ),
            mapOf(
                "id" to "meningococco_b",
                "nome" to "Meningococco B",
                "tipo" to "inattivato",
                "eta_minima" to 0,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf("Immunodeficienza severa"),
                "terapie_controindicate" to listOf<String>(),
                "condizioni_controindicate" to listOf<String>()
            ),
            mapOf(
                "id" to "meningococco_acwy",
                "nome" to "Meningococco ACWY",
                "tipo" to "inattivato",
                "eta_minima" to 0,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf("Immunodeficienza severa"),
                "terapie_controindicate" to listOf<String>(),
                "condizioni_controindicate" to listOf<String>()
            ),
            mapOf(
                "id" to "mprv",
                "nome" to "MPRV (Morbillo-Parotite-Rosolia-Varicella)",
                "tipo" to "vivo_attenuato",
                "eta_minima" to 1,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf<String>(),
                "terapie_controindicate" to listOf("Anti-TNF", "Corticosteroidi alto dosaggio", "Chemioterapia"),
                "condizioni_controindicate" to listOf("Immunodeficienza severa", "Gravidanza")
            ),
            mapOf(
                "id" to "hpv",
                "nome" to "HPV (Anti-Papillomavirus)",
                "tipo" to "ricombinante",
                "eta_minima" to 9,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf("Immunodeficienza severa"),
                "terapie_controindicate" to listOf<String>(),
                "condizioni_controindicate" to listOf("Gravidanza")
            ),
            mapOf(
                "id" to "dtpa_polio",
                "nome" to "dTpa-Polio (Richiamo)",
                "tipo" to "inattivato",
                "eta_minima" to 4,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf("Gravidanza"),
                "terapie_controindicate" to listOf<String>(),
                "condizioni_controindicate" to listOf<String>()
            ),
            mapOf(
                "id" to "varicella",
                "nome" to "Varicella",
                "tipo" to "vivo_attenuato",
                "eta_minima" to 1,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf<String>(),
                "terapie_controindicate" to listOf("Anti-TNF", "Corticosteroidi alto dosaggio", "Chemioterapia"),
                "condizioni_controindicate" to listOf("Immunodeficienza severa", "Gravidanza")
            ),
            mapOf(
                "id" to "shingrix",
                "nome" to "Herpes Zoster (Shingrix)",
                "tipo" to "ricombinante",
                "eta_minima" to 18,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf("Diabete", "Cardiopatia cronica", "Immunodeficienza severa"),
                "terapie_controindicate" to listOf<String>(),
                "condizioni_controindicate" to listOf<String>()
            ),
            mapOf(
                "id" to "antinfluenzale_inattivato",
                "nome" to "Antinfluenzale Inattivato",
                "tipo" to "inattivato",
                "eta_minima" to 0,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf("Diabete", "Cardiopatia cronica", "Asma cronica", "Gravidanza", "Immunodeficienza severa"),
                "terapie_controindicate" to listOf<String>(),
                "condizioni_controindicate" to listOf<String>()
            ),
            mapOf(
                "id" to "antinfluenzale_laiv",
                "nome" to "Antinfluenzale LAIV",
                "tipo" to "vivo_attenuato",
                "eta_minima" to 2,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf<String>(),
                "terapie_controindicate" to listOf("Anti-TNF", "Corticosteroidi alto dosaggio", "Chemioterapia"),
                "condizioni_controindicate" to listOf("Immunodeficienza severa", "Gravidanza", "Asma cronica")
            ),
            mapOf(
                "id" to "febbre_gialla",
                "nome" to "Febbre Gialla",
                "tipo" to "vivo_attenuato",
                "eta_minima" to 0,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf<String>(),
                "terapie_controindicate" to listOf("Anti-TNF", "Corticosteroidi alto dosaggio", "Chemioterapia"),
                "condizioni_controindicate" to listOf("Immunodeficienza severa", "Gravidanza")
            ),
            mapOf(
                "id" to "epatite_a",
                "nome" to "Epatite A",
                "tipo" to "inattivato",
                "eta_minima" to 1,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf<String>(),
                "terapie_controindicate" to listOf<String>(),
                "condizioni_controindicate" to listOf<String>()
            ),
            mapOf(
                "id" to "epatite_b",
                "nome" to "Epatite B",
                "tipo" to "inattivato",
                "eta_minima" to 0,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf("Diabete", "Immunodeficienza severa"),
                "terapie_controindicate" to listOf<String>(),
                "condizioni_controindicate" to listOf<String>()
            ),
            mapOf(
                "id" to "covid19",
                "nome" to "Covid-19 (mRNA)",
                "tipo" to "mrna",
                "eta_minima" to 0,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf("Diabete", "Cardiopatia cronica", "Asma cronica", "Gravidanza", "Immunodeficienza severa"),
                "terapie_controindicate" to listOf<String>(),
                "condizioni_controindicate" to listOf<String>()
            ),
            mapOf(
                "id" to "ppsv23",
                "nome" to "Pneumococco Polisaccaridico (PPSV23)",
                "tipo" to "inattivato",
                "eta_minima" to 2,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf("Diabete", "Cardiopatia cronica", "Asma cronica", "Immunodeficienza severa"),
                "terapie_controindicate" to listOf<String>(),
                "condizioni_controindicate" to listOf<String>()
            ),
            mapOf(
                "id" to "rabbia",
                "nome" to "Antirabico (Rabbia)",
                "tipo" to "inattivato",
                "eta_minima" to 0,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf<String>(),
                "terapie_controindicate" to listOf<String>(),
                "condizioni_controindicate" to listOf<String>()
            ),
            mapOf(
                "id" to "febbre_tifoide_vivo",
                "nome" to "Febbre Tifoide (Orale)",
                "tipo" to "vivo_attenuato",
                "eta_minima" to 5,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf<String>(),
                "terapie_controindicate" to listOf("Anti-TNF", "Corticosteroidi alto dosaggio", "Chemioterapia"),
                "condizioni_controindicate" to listOf("Immunodeficienza severa", "Gravidanza")
            ),
            mapOf(
                "id" to "dengue",
                "nome" to "Dengue (Qdenga)",
                "tipo" to "vivo_attenuato",
                "eta_minima" to 4,
                "eta_massima" to 130,
                "condizioni_raccomandate" to listOf<String>(),
                "terapie_controindicate" to listOf("Anti-TNF", "Corticosteroidi alto dosaggio", "Chemioterapia"),
                "condizioni_controindicate" to listOf("Immunodeficienza severa", "Gravidanza")
            ),
            mapOf(
                "id" to "encefalite_zecca",
                "nome" to "Encefalite da Zecca (TBE)",
                "tipo" to "inattivato",
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