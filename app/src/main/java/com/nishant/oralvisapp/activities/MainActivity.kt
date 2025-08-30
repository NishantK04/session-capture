package com.nishant.oralvisapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nishant.oralvisapp.activities.HomeFragment
import com.nishant.oralvisapp.R
import com.nishant.oralvisapp.activities.SearchFragment
import com.nishant.oralvisapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load default fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomeFragment())
            .commit()

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(
                            R.anim.slide_in_left,  // enter
                            R.anim.slide_out_right // exit
                        )
                        .replace(R.id.fragment_container, HomeFragment())
                        .commit()
                    true
                }
                R.id.nav_search -> {
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(
                            R.anim.slide_in_right, // enter
                            R.anim.slide_out_left  // exit
                        )
                        .replace(R.id.fragment_container, SearchFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }

    }
}