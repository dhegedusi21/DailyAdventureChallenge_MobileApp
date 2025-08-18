package com.example.dailyadventurechallenge.data.repository

import com.example.dailyadventurechallenge.data.api.ApiService
import com.example.dailyadventurechallenge.data.api.RetrofitClient
import com.example.dailyadventurechallenge.data.dto.Challenge.ChallengeResponseDTO
import retrofit2.HttpException
import java.io.IOException
class ChallengeRepository(
    private val apiService: ApiService = RetrofitClient.apiService
) {

    suspend fun getAllChallenges(): Result<List<ChallengeResponseDTO>> {
        return try {
            val response = apiService.getAllChallenges()
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error(HttpException(response))
            }
        } catch (e: IOException) {
            Result.Error(e)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getChallengeById(challengeId: Int): Result<ChallengeResponseDTO> {
        return try {
            val response = apiService.getChallengeById(challengeId)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else if (response.code() == 404) {
                Result.Error(Exception("Challenge not found"))
            } else {
                Result.Error(HttpException(response))
            }
        } catch (e: IOException) {
            Result.Error(e)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getChallengesByDifficulty(difficulty: String): Result<List<ChallengeResponseDTO>> {
        return try {
            val response = apiService.getChallengesByDifficulty(difficulty)
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else if (response.code() == 404) {
                Result.Error(Exception("No challenges found for difficulty: $difficulty"))
            } else {
                Result.Error(HttpException(response))
            }
        } catch (e: IOException) {
            Result.Error(e)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getUserCurrentChallenge(userId: Int): Result<ChallengeResponseDTO> {
        return try {
            val response = apiService.getUserCurrentChallenge(userId)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else if (response.code() == 404) {
                Result.Error(Exception("No active challenge found for today"))
            } else {
                Result.Error(HttpException(response))
            }
        } catch (e: IOException) {
            Result.Error(e)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun assignDailyChallenge(userId: Int): Result<ChallengeResponseDTO> {
        return try {
            val response = apiService.assignDailyChallenge(userId)
            if (response.isSuccessful && response.body() != null) {
                val actualChallenge = response.body()!!.getActualChallenge()
                if (actualChallenge != null) {
                    Result.Success(actualChallenge)
                } else {
                    Result.Error(
                        Exception(
                            response.body()!!.message
                                ?: "Failed to assign or retrieve challenge details"
                        )
                    )
                }
            } else {
                Result.Error(HttpException(response))
            }
        } catch (e: IOException) {
            Result.Error(e)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}