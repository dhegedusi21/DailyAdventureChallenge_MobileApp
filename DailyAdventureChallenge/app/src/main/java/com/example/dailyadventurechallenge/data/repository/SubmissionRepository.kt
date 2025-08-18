package com.example.dailyadventurechallenge.data.repository

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.example.dailyadventurechallenge.data.api.RetrofitClient
import com.example.dailyadventurechallenge.data.dto.Submission.CreateSubmissionDTO
import com.example.dailyadventurechallenge.data.dto.Submission.FileUploadResponseDTO
import com.example.dailyadventurechallenge.data.dto.Submission.SubmissionResponseDTO
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class SubmissionRepository {

    private val apiService = RetrofitClient.apiService

    suspend fun uploadSubmissionPhoto(imageFile: File, context: Context): Result<FileUploadResponseDTO> {
        return try {

            val mimeType = context.contentResolver.getType(Uri.fromFile(imageFile)) ?: "image/*"
            val requestFile = imageFile.asRequestBody(mimeType.toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            val response = apiService.uploadSubmissionPhoto(body)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(Exception("Upload failed: ${response.code()} ${response.message()} - ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.Error(Exception("Upload failed: ${e.message}", e))
        }
    }


    suspend fun createSubmission(userId: Int, challengeId: Int, photoUrl: String): Result<SubmissionResponseDTO> {
        return try {
            val createSubmissionDTO = CreateSubmissionDTO(
                userId = userId,
                challengeId = challengeId,
                photoUrl = photoUrl
            )
            val response = apiService.createSubmission(createSubmissionDTO)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(Exception("Submission creation failed: ${response.code()} ${response.message()} - ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.Error(Exception("Submission creation failed: ${e.message}", e))
        }
    }
}
