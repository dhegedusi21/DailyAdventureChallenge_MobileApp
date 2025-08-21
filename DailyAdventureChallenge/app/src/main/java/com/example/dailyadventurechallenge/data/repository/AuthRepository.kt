package com.example.dailyadventurechallenge.data.repository

import com.example.dailyadventurechallenge.data.api.ApiService
import com.example.dailyadventurechallenge.data.api.RetrofitClient.apiService
import com.example.dailyadventurechallenge.data.dto.Authentication.AuthResponseDTO
import com.example.dailyadventurechallenge.data.dto.Authentication.LoginDTO
import com.example.dailyadventurechallenge.data.dto.Authentication.TokenModel
import com.example.dailyadventurechallenge.data.dto.User.CreateUserDTO
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException

data class ErrorResponse(
    val message: String?,
)

class AuthRepository(private val apiService: ApiService) {
    private val gson = Gson()


    suspend fun login(email: String, password: String): Result<AuthResponseDTO> {
        return try {
            val loginRequest = LoginDTO(email, password)
            val httpResponse: retrofit2.Response<AuthResponseDTO> = apiService.login(loginRequest)

            if (httpResponse.isSuccessful) {
                val authResponse: AuthResponseDTO? = httpResponse.body()
                if (authResponse != null) {
                    if (authResponse.isSuccess) {
                        Result.Success(authResponse)
                    } else {
                        Result.Error(Exception(authResponse.message ?: "Login failed due to server indicated error"))
                    }
                } else {
                    Result.Error(Exception("Empty response body from server during login"))
                }
            } else {
                val errorBody = httpResponse.errorBody()?.string()
                val errorMessage = if (errorBody != null) {
                    try {
                        val errorDto = gson.fromJson(errorBody, ErrorResponse::class.java)
                        errorDto.message ?: "Login failed with code: ${httpResponse.code()} ${httpResponse.message()}"
                    } catch (e: Exception) {
                        "Login failed with code: ${httpResponse.code()} ${httpResponse.message()} (raw error: $errorBody)"
                    }
                } else {
                    "Login failed with code: ${httpResponse.code()} ${httpResponse.message()}"
                }
                Result.Error(Exception(errorMessage))
            }
        } catch (e: HttpException) {
            Result.Error(Exception("Login network request failed: ${e.message()}", e))
        } catch (e: IOException) {
            Result.Error(Exception("Login network connection error. Please check your internet.", e))
        } catch (e: Exception) {
            Result.Error(Exception("An unexpected error occurred: ${e.message}", e))
        }
    }
    suspend fun registerUser(createUserRequest: CreateUserDTO): Result<AuthResponseDTO> {
        return try {
            val httpResponse: retrofit2.Response<AuthResponseDTO> = apiService.register(createUserRequest)

            if (httpResponse.isSuccessful) {
                val authResponse: AuthResponseDTO? = httpResponse.body()
                if (authResponse != null) {
                    if (authResponse.isSuccess) {
                        Result.Success(authResponse)
                    } else {
                        Result.Error(Exception(authResponse.message ?: "Registration failed due to server error"))
                    }
                } else {
                    Result.Error(Exception("Empty response body from server"))
                }
            } else {
                val errorBody = httpResponse.errorBody()?.string()
                val errorMessage = if (!errorBody.isNullOrBlank()) {
                    try {
                        val errorDto = gson.fromJson(errorBody, ErrorResponse::class.java) // Using your existing ErrorResponse
                        errorDto.message ?: "Registration failed with code: ${httpResponse.code()} ${httpResponse.message()}"
                    } catch (e: Exception) {
                        try {
                            val authErrorDto = gson.fromJson(errorBody, AuthResponseDTO::class.java)
                            authErrorDto.message ?: "Registration failed with code: ${httpResponse.code()} (raw error: $errorBody)"
                        } catch (e2: Exception) {
                            "Registration failed with code: ${httpResponse.code()} (raw error: $errorBody)"
                        }
                    }
                } else {
                    "Registration failed with code: ${httpResponse.code()} ${httpResponse.message()}"
                }
                Result.Error(Exception(errorMessage))
            }
        } catch (e: HttpException) {
            Result.Error(Exception("Registration network request failed: ${e.message()}", e))
        } catch (e: IOException) {
            Result.Error(Exception("Registration network connection error. Please check your internet.", e))
        } catch (e: Exception) {
            Result.Error(Exception("An unexpected error occurred during registration: ${e.message}", e))
        }
    }
    suspend fun refreshToken(accessToken: String, refreshToken: String): Result<AuthResponseDTO> {
        return try {
            val tokenModel = TokenModel(accessToken, refreshToken)
            val httpResponse: retrofit2.Response<AuthResponseDTO> = apiService.refreshToken(tokenModel)

            if (httpResponse.isSuccessful) {
                val authResponse: AuthResponseDTO? = httpResponse.body()
                if (authResponse != null) {
                    if (authResponse.isSuccess) {
                        Result.Success(authResponse)
                    } else {
                        Result.Error(Exception(authResponse.message ?: "Token refresh failed due to server indicated error"))
                    }
                } else {
                    Result.Error(Exception("Empty response body from token refresh"))
                }
            } else {
                val errorBody = httpResponse.errorBody()?.string()
                val errorMessage = if (errorBody != null) {
                    try {
                        val errorDto = gson.fromJson(errorBody, ErrorResponse::class.java)
                        errorDto.message ?: "Token refresh failed with code: ${httpResponse.code()} ${httpResponse.message()}"
                    } catch (e: Exception) {
                        "Token refresh failed with code: ${httpResponse.code()} ${httpResponse.message()} (raw error: $errorBody)"
                    }
                } else {
                    "Token refresh failed with code: ${httpResponse.code()} ${httpResponse.message()}"
                }
                Result.Error(Exception(errorMessage))
            }
        } catch (e: HttpException) {
            Result.Error(Exception("Token refresh network request failed: ${e.message()}", e))
        } catch (e: IOException) {
            Result.Error(Exception("Network error during token refresh. Please check your internet.", e))
        } catch (e: Exception) {
            Result.Error(Exception("An unexpected error occurred during token refresh: ${e.message}", e))
        }
    }
}
