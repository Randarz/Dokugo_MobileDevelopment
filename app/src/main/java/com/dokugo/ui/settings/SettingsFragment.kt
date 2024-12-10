package com.dokugo.ui.settings

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.NavOptions
import com.dokugo.R
import com.dokugo.databinding.FragmentSettingsBinding
import com.dokugo.login.signin.SignInActivity

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

        binding.changeEmailContainer.setOnClickListener {
            // Pastikan konteks Fragment valid
            val builder = AlertDialog.Builder(requireContext())

            // Mengambil pesan dari string.xml
            val message = getString(R.string.change_message)
            val description = getString(R.string.dialog_description)

            // Membuat pesan dengan format HTML menggunakan Html.fromHtml()
            val fullMessage = Html.fromHtml("<b>$message</b><br><i>$description</i>", Html.FROM_HTML_MODE_LEGACY)

            builder.setMessage(fullMessage)
                .setCancelable(false) // Tidak bisa dibatalkan dengan menekan di luar dialog
                .setPositiveButton("Ya") { _, _ ->
                    // Aksi saat menekan tombol "Ya"
                    val intent = Intent(requireContext(), SignInActivity::class.java)
                    startActivity(intent)
                    activity?.finish() // Menutup Activity jika perlu
                }
                .setNegativeButton("Tidak") { dialog, _ ->
                    // Menutup dialog jika tombol "Tidak" ditekan
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show() // Menampilkan dialog
        }


        binding.deleteAccountContainer.setOnClickListener {
            // Pastikan konteks Fragment valid
            val builder = AlertDialog.Builder(requireContext())

            // Mengambil pesan dari string.xml
            val message = getString(R.string.delete_message)
            val description = getString(R.string.dialog_description)

            // Membuat pesan dengan format HTML menggunakan Html.fromHtml()
            val fullMessage = Html.fromHtml("<b>$message</b><br><i>$description</i>", Html.FROM_HTML_MODE_LEGACY)

            builder.setMessage(fullMessage)
                .setCancelable(false) // Tidak bisa dibatalkan dengan menekan di luar dialog
                .setPositiveButton("Ya") { _, _ ->
                    // Aksi saat menekan tombol "Ya"
                    val intent = Intent(requireContext(), SignInActivity::class.java)
                    startActivity(intent)
                    activity?.finish() // Menutup Activity jika perlu
                }
                .setNegativeButton("Tidak") { dialog, _ ->
                    // Menutup dialog jika tombol "Tidak" ditekan
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show() // Menampilkan dialog
        }





        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
