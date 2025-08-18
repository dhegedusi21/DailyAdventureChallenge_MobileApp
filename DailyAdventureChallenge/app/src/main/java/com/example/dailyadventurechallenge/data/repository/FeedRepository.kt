package com.example.dailyadventurechallenge.data.repository

import com.example.dailyadventurechallenge.data.api.ApiService
import com.example.dailyadventurechallenge.data.dto.Feed.SubmissionItem
import com.example.dailyadventurechallenge.data.dto.Feed.CreateVoteRequest
import com.example.dailyadventurechallenge.data.dto.Feed.VoteResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}

class FeedRepository(private val apiService: ApiService) {

    suspend fun getFeedSubmissions(): Result<List<SubmissionItem>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getFeedSubmissions()
                if (response.isSuccessful) {
                    val allSubmissions = response.body() ?: emptyList()
                    Result.Success(allSubmissions)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
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

    suspend fun postVote(submissionId: Int, userId: Int, voteStatus: String): Result<VoteResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val voteRequest = CreateVoteRequest(submissionId = submissionId, userId = userId, voteStatus = voteStatus)
                val response = apiService.postVote(voteRequest)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Result.Success(it)
                    } ?: Result.Error(Exception("Empty response body for postVote"))
                } else {
                    Result.Error(HttpException(response))
                }
            } catch (e: IOException) {
                Result.Error(e)
            } catch (e: HttpException) {
                Result.Error(e)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }


    suspend fun getUserVoteForSubmission(userId: Int, submissionId: Int): Result<VoteResponse?> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getUserVoteForSubmission(userId = userId, submissionId = submissionId)
                if (response.isSuccessful) {
                    Result.Success(response.body())
                } else {
                    if (response.code() == 404) {
                        Result.Success(null)
                    } else {
                        Result.Error(HttpException(response))
                    }
                }
            } catch (e: IOException) {
                Result.Error(e)
            } catch (e: HttpException) {
                if (e.code() == 404) {
                    Result.Success(null)
                } else {
                    Result.Error(e)
                }
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }
}
