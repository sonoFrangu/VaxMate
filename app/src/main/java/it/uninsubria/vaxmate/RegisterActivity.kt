package it.uninsubria.vaxmate

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import it.uninsubria.vaxmate.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ospedali = arrayOf(
            "Ospedale di Circolo Varese",
            "Ospedale Del Ponte",
            "Ospedale di Como"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            ospedali
        )

        binding.etHospital.setAdapter(adapter)

        binding.etHospital.setOnClickListener {
            binding.etHospital.showDropDown()
        }

        binding.btnDoRegister.setOnClickListener {

            val pass1 = binding.etRegisterPassword.text.toString()
            val pass2 = binding.etConfirmPassword.text.toString()

            if (pass1 != pass2) {

                Log.e(
                    "VaxMate_Debug",
                    "Password diverse"
                )

            } else {

                Log.d(
                    "VaxMate_Debug",
                    "Registrazione completata"
                )

                startActivity(
                    Intent(this, MainActivity::class.java)
                )
            }
        }

        binding.tvGoToLogin.setOnClickListener {
            finish()
        }
    }
}