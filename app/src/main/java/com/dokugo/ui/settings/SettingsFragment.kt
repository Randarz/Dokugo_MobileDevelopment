package com.dokugo.ui.settings

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.NavOptions
import com.bumptech.glide.Glide
import com.dokugo.R
import com.dokugo.data.network.RetrofitInstance
import com.dokugo.data.repository.UserRepository
import com.dokugo.databinding.FragmentSettingsBinding
import com.dokugo.login.signin.SignInActivity
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private lateinit var userRepository: UserRepository

    private val MAXIMAL_SIZE = 1 * 1024 * 1024 // 1MB

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        navController = findNavController()
        userRepository = UserRepository(RetrofitInstance.api)

        loadProfile()

        binding.editAccountContainer.setOnClickListener {
            val bundle = Bundle().apply {
                putBoolean("cameFromSettings", true)
            }

            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.navigation_settings, true)
                .build()

            navController.navigate(R.id.action_settingsFragment_to_editProfileFragment, bundle, navOptions)
        }

        binding.logoutContainer.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            val message = getString(R.string.change_message)
            val description = getString(R.string.dialog_description)
            val fullMessage = Html.fromHtml("<b>$message</b><br><i>$description</i>", Html.FROM_HTML_MODE_LEGACY)

            builder.setMessage(fullMessage)
                .setCancelable(false)
                .setPositiveButton("Ya") { _, _ ->
                    logout()
                }
                .setNegativeButton("Tidak") { dialog, _ ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }

        binding.deleteAccountContainer.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            val message = getString(R.string.delete_message)
            val description = getString(R.string.dialog_description)
            val fullMessage = Html.fromHtml("<b>$message</b><br><i>$description</i>", Html.FROM_HTML_MODE_LEGACY)

            builder.setMessage(fullMessage)
                .setCancelable(false)
                .setPositiveButton("Ya") { _, _ ->
                    deleteProfile()
                }
                .setNegativeButton("Tidak") { dialog, _ ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }

        binding.changePhotoContainer.setOnClickListener {
            requestStoragePermission()
        }

        return root
    }

    private fun requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        } else {
            openGallery()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val selectedImageUri: Uri? = data?.data
            selectedImageUri?.let {
                val filePath = getRealPathFromURI(it)
                if (filePath != null) {
                    val file = File(filePath)
                    val compressedFile = file.reduceFileImage()
                    uploadPhoto(compressedFile)
                } else {
                    Toast.makeText(requireContext(), "Failed to get image path", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getRealPathFromURI(contentUri: Uri): String? {
        var result: String? = null
        val cursor = requireContext().contentResolver.query(contentUri, null, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                result = cursor.getString(idx)
            }
            cursor.close()
        }
        return result
    }

    private fun File.reduceFileImage(): File {
        val bitmap = BitmapFactory.decodeFile(this.path)
        var compressQuality = 100
        var streamLength: Int

        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > MAXIMAL_SIZE && compressQuality > 0)

        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(this))
        return this
    }

    private fun uploadPhoto(file: File) {
        lifecycleScope.launch {
            try {
                val token = getTokenFromSharedPreferences()
                val response = userRepository.updateProfilePhoto(token, file)
                if (!response.error) {
                    Toast.makeText(requireContext(), "Photo uploaded successfully", Toast.LENGTH_SHORT).show()
                    loadProfile()
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to upload photo: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getTokenFromSharedPreferences(): String {
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("auth_token", "") ?: ""
    }

    private fun loadProfile() {
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", null)
        val email = sharedPreferences.getString("email", null)
        val phoneNumber = sharedPreferences.getString("phone_number", null)
        val avatarUrl = sharedPreferences.getString("avatar_url", null)

        if (username != null && email != null && avatarUrl != null) {
            binding.tvUsername.text = username
            binding.tvEmail.text = email
            Glide.with(this).load(avatarUrl).into(binding.imgProfile)
        } else {
            getProfile()
        }
    }

    private fun getProfile() {
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null)

        if (token != null) {
            lifecycleScope.launch {
                try {
                    val response = userRepository.getProfile(token)
                    binding.tvUsername.text = response.user.username
                    binding.tvEmail.text = response.user.email
                    Glide.with(this@SettingsFragment)
                        .load(response.user.avatarUrl)
                        .into(binding.imgProfile)

                    // Save profile data in SharedPreferences
                    sharedPreferences.edit().apply {
                        putString("username", response.user.username)
                        putString("email", response.user.email)
                        putString("phone_number", response.user.phoneNumber)
                        putString("avatar_url", response.user.avatarUrl)
                        apply()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Failed to load profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "No token found. Please log in again.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteProfile() {
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null)

        if (token != null) {
            lifecycleScope.launch {
                try {
                    val response = userRepository.deleteProfile(token)
                    if (!response.error) {
                        Toast.makeText(requireContext(), "Profile deleted successfully", Toast.LENGTH_SHORT).show()
                        logout()
                    } else {
                        Toast.makeText(requireContext(), "Error: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Failed to delete profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "No token found. Please log in again.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearLocalData() {
        // Clear shared preferences
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        // Clear any other local data if necessary
        // For example, delete files or clear databases
    }

    private fun logout() {
        clearLocalData()
        val intent = Intent(requireContext(), SignInActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}