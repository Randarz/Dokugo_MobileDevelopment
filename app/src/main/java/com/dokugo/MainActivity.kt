package com.dokugo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.dokugo.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up NavController for navigating between fragments
        val navController: NavController = findNavController(R.id.nav_host_fragment_activity_main)

        // Set up BottomNavigationView to work with the NavController
        val bottomNavView: BottomNavigationView = binding.navView
        bottomNavView.setupWithNavController(navController)

        // Optional: Handling the back button behavior for the BottomNavigationView

    }
}
