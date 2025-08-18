package com.example.dailyadventurechallenge.data.repository

import com.example.dailyadventurechallenge.data.api.ApiService
import com.example.dailyadventurechallenge.data.dto.Achievement.AchievementResponseDTO
import com.example.dailyadventurechallenge.data.dto.Achievement.UserAchievementResponseDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class AchievementRepository(private val apiService: ApiService) {

    suspend fun getAllAchievements(): Result<List<AchievementResponseDTO>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAllAchievements()
                if (response.isSuccessful) {
                    Result.Success(response.body() ?: emptyList())
                } else {
                    Result.Error(HttpException(response))
                }
            } catch (e: HttpException) {
                Result.Error(e)
            } catch (e: IOException) {
                Result.Error(e)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }

    suspend fun getUserAchievements(userId: Int): Result<List<UserAchievementResponseDTO>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getUserAchievements(userId)
                if (response.isSuccessful) {
                    Result.Success(response.body() ?: emptyList())
                } else {
                    Result.Error(HttpException(response))
                }
            } catch (e: HttpException) {
                Result.Error(e)
            } catch (e: IOException) {
                Result.Error(e)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }
}
