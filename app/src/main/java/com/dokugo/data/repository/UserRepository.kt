package com.dokugo.data.repository

import com.dokugo.data.network.ApiService
import com.dokugo.data.response.GenericResponse
import com.dokugo.data.response.LoginResponse
import com.dokugo.data.response.ProfileResponse
import com.dokugo.data.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import java.io.File

class UserRepository(private val apiService: ApiService) {

    suspend fun loginUser(email: String, password: String): LoginResponse {
        return apiService.loginUser(email, password)
    }

    suspend fun registerUser(
        username: String,
        email: String,
        password: String,
        phoneNumber: String
    ): RegisterResponse {
        return apiService.registerUser(username, email, password, phoneNumber)
    }

    suspend fun getProfile(token: String): ProfileResponse {
        return apiService.getProfile("Bearer $token")
    }

    suspend fun updateProfile(
        token: String,
        username: String,
        email: String,
        phoneNumber: String
    ): GenericResponse {
        val profile = mapOf(
            "username" to username,
            "email" to email,
            "phone_number" to phoneNumber
        )
        return apiService.updateProfile("Bearer $token", profile)
    }

    suspend fun deleteProfile(token: String): GenericResponse {
        return apiService.deleteProfile("Bearer $token")
    }

    suspend fun updateProfilePhoto(token: String, file: File): GenericResponse {
        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
        val body = MultipartBody.Part.createFormData("photo", file.name, requestFile)
        return apiService.updateProfilePhoto("Bearer $token", body)
    }

    suspend fun sendOtp(email: String): GenericResponse {
        val emailMap = mapOf("email" to email)
        return apiService.sendOtp(emailMap)
    }

    suspend fun verifyOtp(email: String, otp: String): GenericResponse {
        val otpData = mapOf("email" to email, "otp" to otp)
        return apiService.verifyOtp(otpData)
    }

    suspend fun resetPassword(resetToken: String, newPassword: String): GenericResponse {
        val resetData = mapOf("resetToken" to resetToken, "newPassword" to newPassword)
        return apiService.resetPassword(resetData)
    }
}