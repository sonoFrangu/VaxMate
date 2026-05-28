package it.uninsubria.vaxmate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import it.uninsubria.vaxmate.databinding.ActivityDoctorLoginBinding

class DoctorLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDoctorLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDoLogin.setOnClickListener {

            val emailInserita = binding.etEmail.text.toString()
            val passwordInserita = binding.etPassword.text.toString()

            Log.d("VaxMate_Debug", "--- TENTATIVO LOGIN ---")
            Log.d("VaxMate_Debug", emailInserita)

            // todo TEMPORANEO
            startActivity(
                Intent(this, MainActivity::class.java)
            )
        }
    }
}