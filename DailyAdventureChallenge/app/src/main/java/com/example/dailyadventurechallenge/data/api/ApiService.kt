package com.example.dailyadventurechallenge.data.api

import com.example.dailyadventurechallenge.data.dto.Achievement.AchievementResponseDTO
import com.example.dailyadventurechallenge.data.dto.Achievement.UserAchievementResponseDTO
import com.example.dailyadventurechallenge.data.dto.Authentication.AuthResponseDTO
import com.example.dailyadventurechallenge.data.dto.Authentication.LoginDTO
import com.example.dailyadventurechallenge.data.dto.Authentication.TokenModel
import com.example.dailyadventurechallenge.data.dto.Challenge.AssignChallengeApiResponse
import com.example.dailyadventurechallenge.data.dto.Challenge.ChallengeResponseDTO
import com.example.dailyadventurechallenge.data.dto.Submission.CreateSubmissionDTO
import com.example.dailyadventurechallenge.data.dto.Submission.FileUploadResponseDTO
import com.example.dailyadventurechallenge.data.dto.Submission.SubmissionResponseDTO
import com.example.dailyadventurechallenge.data.dto.User.UserResponseDTO
import com.example.dailyadventurechallenge.data.dto.Feed.SubmissionItem
import com.example.dailyadventurechallenge.data.dto.Feed.CreateVoteRequest
import com.example.dailyadventurechallenge.data.dto.Feed.VoteResponse
import com.example.dailyadventurechallenge.data.dto.User.FcmTokenRequest
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // Auth
    @POST("api/Auth/login")
    suspend fun login(@Body loginRequest: LoginDTO): Response<AuthResponseDTO>

    @POST("api/Auth/refresh-token")
    suspend fun refreshToken(@Body tokenModel: TokenModel): Response<AuthResponseDTO>


    // User
    @GET("api/User/{id}")
    suspend fun getUserById(@Path("id") userId: Int): Response<UserResponseDTO>


    // Challenge
    @GET("api/Challenge")
    suspend fun getAllChallenges(): Response<List<ChallengeResponseDTO>>

    @GET("api/Challenge/{id}")
    suspend fun getChallengeById(@Path("id") challengeId: Int): Response<ChallengeResponseDTO>

    @GET("api/Challenge/difficulty/{difficulty}")
    suspend fun getChallengesByDifficulty(@Path("difficulty") difficulty: String): Response<List<ChallengeResponseDTO>>

    @GET("api/Challenge/user/{userId}/current")
    suspend fun getUserCurrentChallenge(@Path("userId") userId: Int): Response<ChallengeResponseDTO>

    @POST("api/Challenge/assign")
    suspend fun assignDailyChallenge(@Query("userId") userId: Int): Response<AssignChallengeApiResponse>


    // Submission
    @Multipart
    @POST("api/FileUpload/upload-submission")
    suspend fun uploadSubmissionPhoto(@Part photo: MultipartBody.Part): Response<FileUploadResponseDTO>

    @POST("api/Submission")
    suspend fun createSubmission(@Body submissionDto: CreateSubmissionDTO): Response<SubmissionResponseDTO>


    // News Feed and Voting
    @GET("api/Submission")
    suspend fun getFeedSubmissions(): Response<List<SubmissionItem>>

    @POST("api/Vote")
    suspend fun postVote(@Body voteRequest: CreateVoteRequest): Response<VoteResponse>

    @GET("api/Vote/user/{userId}/submission/{submissionId}")
    suspend fun getUserVoteForSubmission(
        @Path("userId") userId: Int,
        @Path("submissionId") submissionId: Int
    ): Response<VoteResponse>

    // Activements
    @GET("api/Achievement")
    suspend fun getAllAchievements(): Response<List<AchievementResponseDTO>>

    @GET("api/UserAchievement/user/{userId}")
    suspend fun getUserAchievements(@Path("userId") userId: Int): Response<List<UserAchievementResponseDTO>>

    @POST("api/notification/register-fcm-token")
    suspend fun registerFcmToken(
        @Query("userId") userId: Int,
        @Body fcmTokenRequest: FcmTokenRequest
    ): Response<Unit>
}

