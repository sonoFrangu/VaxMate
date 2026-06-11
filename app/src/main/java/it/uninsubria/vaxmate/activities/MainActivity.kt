package it.uninsubria.vaxmate.activities

import android.content.Intent
import android.os.Bundle
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import it.uninsubria.vaxmate.fragments.AccountFragment
import it.uninsubria.vaxmate.fragments.HomeFragment
import it.uninsubria.vaxmate.fragments.InventoryFragment
import it.uninsubria.vaxmate.fragments.LineeGuidaFragment
import it.uninsubria.vaxmate.R
import it.uninsubria.vaxmate.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {

    //TODO da ancorare: activitydoctorlogin, activitymain, activityregister, fragmentaccount, freagmenthome, fragmentlineeguida
    // anche se non so se vadano ancorati o meno

    private lateinit var binding: ActivityMainBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupLanguageButton(binding.languageButton.btnLanguage)

        binding.imgLogo.setOnClickListener { view ->
            val popupMenu = PopupMenu(this, view)
            popupMenu.menuInflater.inflate(R.menu.logo_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_logout -> {
                        auth.signOut()
                        val intent = Intent(this, LoginActivity::class.java)

                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                        startActivity(intent)
                        finish()
                        true
                    }
                    R.id.menu_linee_guida ->{
                        cambiaFragment(LineeGuidaFragment())
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

        if (savedInstanceState == null) {
            cambiaFragment(HomeFragment())
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    cambiaFragment(HomeFragment())
                    true
                }
                R.id.nav_inventory -> {
                    cambiaFragment(InventoryFragment())
                    true
                }
                R.id.nav_account -> {
                    cambiaFragment(AccountFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun cambiaFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}