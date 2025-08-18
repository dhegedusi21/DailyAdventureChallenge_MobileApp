package com.example.dailyadventurechallenge.data.dto.Submission

import com.google.gson.annotations.SerializedName

data class FileUploadResponseDTO(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("url")
    val url: String?,
    @SerializedName("publicId")
    val publicId: String?
)
