package com.dokugo.ui.settings

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dokugo.R
import com.dokugo.data.repository.UserRepository
import com.dokugo.data.network.RetrofitInstance
import com.dokugo.databinding.FragmentEditProfileBinding
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var userRepository: UserRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        userRepository = UserRepository(RetrofitInstance.api)

        loadProfile()

        binding.btnSave.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val phoneNumber = binding.etPhoneNumber.text.toString().trim()
            updateProfile(username, email, phoneNumber)
        }

        return root
    }

    private fun loadProfile() {
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", null)
        val email = sharedPreferences.getString("email", null)
        val phoneNumber = sharedPreferences.getString("phone_number", null)

        if (username != null && email != null && phoneNumber != null) {
            binding.etUsername.setText(username)
            binding.etEmail.setText(email)
            binding.etPhoneNumber.setText(phoneNumber)
        } else {
            getProfile()
        }
    }

    private fun getProfile() {
        lifecycleScope.launch {
            try {
                val token = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE).getString("auth_token", null) ?: ""
                val response = userRepository.getProfile(token)
                binding.etUsername.setText(response.user.username)
                binding.etEmail.setText(response.user.email)
                binding.etPhoneNumber.setText(response.user.phoneNumber)

                // Save profile data in SharedPreferences
                val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                sharedPreferences.edit().apply {
                    putString("username", response.user.username)
                    putString("email", response.user.email)
                    putString("phone_number", response.user.phoneNumber)
                    apply()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to load profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateProfile(username: String, email: String, phoneNumber: String) {
        lifecycleScope.launch {
            try {
                val token = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE).getString("auth_token", null) ?: ""
                val response = userRepository.updateProfile(token, username, email, phoneNumber)
                if (!response.error) {
                    // Update SharedPreferences with new profile data
                    val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    sharedPreferences.edit().apply {
                        putString("username", username)
                        putString("email", email)
                        putString("phone_number", phoneNumber)
                        apply()
                    }
                    Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    // Navigate back to SettingsFragment
                    findNavController().navigate(R.id.action_editProfileFragment_to_settingsFragment)
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to update profile: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateProfilePhoto(photoFile: File) {
        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), photoFile)
        val body = MultipartBody.Part.createFormData("photo", photoFile.name, requestFile)

        lifecycleScope.launch {
            try {
                val token = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE).getString("auth_token", null) ?: ""
                val response = userRepository.updateProfilePhoto(token, photoFile)
                if (!response.error) {
                    Toast.makeText(requireContext(), "Profile photo updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to update profile photo: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getPhotoFile(): File? {
        // Implement logic to get the photo file
        return null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}