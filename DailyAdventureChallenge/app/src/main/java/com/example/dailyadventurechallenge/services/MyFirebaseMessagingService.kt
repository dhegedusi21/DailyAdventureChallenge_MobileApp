package com.example.dailyadventurechallenge.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.edit
import com.example.dailyadventurechallenge.MainActivity
import com.example.dailyadventurechallenge.R
import com.example.dailyadventurechallenge.data.api.ApiService
import com.example.dailyadventurechallenge.data.api.RetrofitClient
import com.example.dailyadventurechallenge.data.dto.User.FcmTokenRequest
import com.example.dailyadventurechallenge.data.session.SessionManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private lateinit var sessionManager: SessionManager
    private lateinit var apiService: ApiService

    override fun onCreate() {
        super.onCreate()
        sessionManager = SessionManager(applicationContext)
        apiService = RetrofitClient.apiService
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        getSharedPreferences("fcm_prefs", Context.MODE_PRIVATE).edit {
            putString(KEY_UNSENT_FCM_TOKEN, token)
            apply()
        }
        sendRegistrationToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)

        }

        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            sendNotification(it.title, it.body)
        }
    }

    private fun sendRegistrationToServer(token: String?) {
        if (token == null) {
            Log.e(TAG, "FCM Token is null, cannot send to server.")
            return
        }

        val userId = sessionManager.getUserId()
        if (!sessionManager.isLoggedIn() || userId == SessionManager.INVALID_USER_ID) {
            Log.e(TAG, "User not logged in or invalid User ID ($userId), cannot send FCM token to server.")
            return
        }

        Log.d(TAG, "Attempting to send FCM token for userId: $userId")
        val requestBody = FcmTokenRequest(token = token)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.registerFcmToken(userId, requestBody)
                if (response.isSuccessful) {
                } else {
                    Log.d(TAG, "FCM Token registered successfully with backend.")
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "Failed to register FCM token. Code: ${response.code()}, Message: ${response.message()}, Error: $errorBody")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception when trying to register FCM token: ${e.message}", e)
            }
        }
    }

    private fun sendNotification(messageTitle: String?, messageBody: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_ONE_SHOT
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0 , intent,
            pendingIntentFlags
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(messageTitle ?: getString(R.string.app_name))
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = getString(R.string.default_notification_channel_name)
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
        const val KEY_UNSENT_FCM_TOKEN = "unsent_fcm_token"
    }
}
