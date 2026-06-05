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
}