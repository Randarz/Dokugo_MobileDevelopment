package com.dokugo.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.NavOptions
import com.dokugo.R
import com.dokugo.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize NavController
        navController = findNavController()

        // Set an OnClickListener on the LinearLayout (account form)
        binding.editAccountContainer.setOnClickListener {
            // Navigate to EditProfileFragment, passing 'cameFromSettings' flag
            val bundle = Bundle().apply {
                putBoolean("cameFromSettings", true)
            }

            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.navigation_settings, true)
                .build()

            // Navigate to EditProfileFragment with the flag
            navController.navigate(R.id.action_settingsFragment_to_editProfileFragment, bundle, navOptions)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
