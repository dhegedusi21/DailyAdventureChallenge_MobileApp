package com.example.dailyadventurechallenge.ui.Submission

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dailyadventurechallenge.data.dto.Submission.FileUploadResponseDTO
import com.example.dailyadventurechallenge.data.dto.Submission.SubmissionResponseDTO
import com.example.dailyadventurechallenge.data.repository.Result
import com.example.dailyadventurechallenge.data.repository.SubmissionRepository
import com.example.dailyadventurechallenge.data.session.SessionManager
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class SubmissionViewModel(application: Application) : AndroidViewModel(application) {

    private val submissionRepository = SubmissionRepository()
    private val sessionManager = SessionManager(application.applicationContext)

    private val _selectedImageUri = MutableLiveData<Uri?>()
    val selectedImageUri: LiveData<Uri?> = _selectedImageUri

    private val _imageUploadResult = MutableLiveData<Result<FileUploadResponseDTO>?>()
    val imageUploadResult: LiveData<Result<FileUploadResponseDTO>?> = _imageUploadResult

    private val _submissionResult = MutableLiveData<Result<SubmissionResponseDTO>?>()
    val submissionResult: LiveData<Result<SubmissionResponseDTO>?> = _submissionResult

    private val _isLoadingImageUpload = MutableLiveData<Boolean>(false)
    val isLoadingImageUpload: LiveData<Boolean> = _isLoadingImageUpload

    private val _isLoadingCreateSubmission = MutableLiveData<Boolean>(false)
    val isLoadingCreateSubmission: LiveData<Boolean> = _isLoadingCreateSubmission

    fun setSelectedImageUri(uri: Uri?) {
        _selectedImageUri.value = uri
        _imageUploadResult.value = null
        _submissionResult.value = null
    }

    fun uploadAndCreateSubmission(challengeId: Int) {
        val imageUri = _selectedImageUri.value ?: return
        val userId = sessionManager.getUserId()
        if (userId == SessionManager.INVALID_USER_ID) {
            _submissionResult.value = Result.Error(Exception("User not logged in or user ID not found"))
            return
        }

        viewModelScope.launch {
            _isLoadingImageUpload.value = true
            val imageFile = getFileFromUri(
                getApplication<Application>().applicationContext,
                imageUri,
                "submission_image"
            )
            if (imageFile == null) {
                _imageUploadResult.value =
                    Result.Error(Exception("Could not create image file from URI."))
                _isLoadingImageUpload.value = false
                return@launch
            }

            val uploadResult = submissionRepository.uploadSubmissionPhoto(
                imageFile,
                getApplication<Application>().applicationContext
            )
            _imageUploadResult.value = uploadResult
            _isLoadingImageUpload.value = false

            when (uploadResult) {
                is Result.Success -> {
                    val photoUrl = uploadResult.data.url
                    if (photoUrl != null) {
                        _isLoadingCreateSubmission.value = true
                        val createResult =
                            submissionRepository.createSubmission(userId, challengeId, photoUrl)
                        _submissionResult.value = createResult
                        _isLoadingCreateSubmission.value = false
                    } else {
                        _submissionResult.value =
                            Result.Error(Exception("Photo URL was null after successful upload."))
                    }
                }
                is Result.Error -> {
                    _submissionResult.value = Result.Error(
                        uploadResult.exception
                    )
                }
            }
            imageFile.delete()
        }
    }

    private fun getFileFromUri(context: Context, uri: Uri, fileNamePrefix: String): File? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile(
                fileNamePrefix,
                getFileExtension(context, uri),
                context.cacheDir
            )
            tempFile.deleteOnExit()
            val fos = FileOutputStream(tempFile)
            inputStream?.copyTo(fos)
            fos.close()
            inputStream?.close()
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getFileExtension(context: Context, uri: Uri): String? {
        return context.contentResolver.getType(uri)?.let { mimeType ->
            "." + mimeType.substringAfterLast('/', ".tmp")
        } ?: ".tmp"
    }

    fun resetSubmissionState() {
        _selectedImageUri.value = null
        _imageUploadResult.value = null
        _submissionResult.value = null
        _isLoadingImageUpload.value = false
        _isLoadingCreateSubmission.value = false
    }
}
