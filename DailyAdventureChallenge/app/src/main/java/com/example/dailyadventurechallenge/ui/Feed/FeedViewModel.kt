package com.example.dailyadventurechallenge.ui.Feed

import android.app.Application
import androidx.compose.animation.core.copy
import androidx.compose.foundation.layout.size
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dailyadventurechallenge.data.repository.FeedRepository
import com.example.dailyadventurechallenge.data.dto.Feed.SubmissionItem
import com.example.dailyadventurechallenge.data.repository.Result
import com.example.dailyadventurechallenge.data.session.SessionManager
import kotlinx.coroutines.launch

class FeedViewModel(
    application: Application,
    private val feedRepository: FeedRepository
) : AndroidViewModel(application) {

    private val _feedItems = MutableLiveData<List<SubmissionItem>>()
    val feedItems: LiveData<List<SubmissionItem>> = _feedItems

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val sessionManager = SessionManager(application.applicationContext)
    var currentUserId: Int = SessionManager.INVALID_USER_ID

    init {
        currentUserId = sessionManager.getUserId()
        if (currentUserId != SessionManager.INVALID_USER_ID) {
            getFeed()
        } else {
            _error.value = "User not logged in. Cannot load feed."
        }
    }
    fun getFeed() {
        if (currentUserId == SessionManager.INVALID_USER_ID) {
            _error.value = "Cannot fetch feed: User ID is invalid or user not logged in."
            _feedItems.value = emptyList()
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = feedRepository.getFeedSubmissions()) {
                is Result.Success -> {
                    _feedItems.value = result.data
                    _error.value = null
                    result.data.forEachIndexed { index, submission ->
                        fetchUserVoteStatusForSubmission(submission, index)
                    }
                }
                is Result.Error -> {
                    _error.value = "Error fetching feed: ${result.exception.message}"
                    _feedItems.value = emptyList()
                }
            }
            _isLoading.value = false
        }
    }

    private fun fetchUserVoteStatusForSubmission(submission: SubmissionItem, itemIndex: Int) {
        if (currentUserId == SessionManager.INVALID_USER_ID) return

        viewModelScope.launch {
            when (val voteResult = feedRepository.getUserVoteForSubmission(currentUserId, submission.idSubmission)) {
                is Result.Success -> {
                    val currentItems = _feedItems.value?.toMutableList()
                    currentItems?.let { items ->
                        if (itemIndex < items.size) {
                            items[itemIndex] = items[itemIndex].copy(currentUserVoteStatus = voteResult.data?.voteStatus)
                            _feedItems.value = items
                        }
                    }
                }
                is Result.Error -> {
                    println("Error fetching vote status for submission ${submission.idSubmission}: ${voteResult.exception.message}")
                }
            }
        }
    }

    fun voteOnSubmission(submissionId: Int, voteStatus: String) {
        if (currentUserId == SessionManager.INVALID_USER_ID) {
            _error.value = "Cannot vote: User ID is invalid or user not logged in."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            when (val result = feedRepository.postVote(submissionId, currentUserId, voteStatus)) {
                is Result.Success -> {

                    getFeed()
                    _error.value = null
                }
                is Result.Error -> {
                    _error.value = "Error voting: ${result.exception.message}"
                }
            }
            _isLoading.value = false
        }
    }

    fun refreshFeed() {
        if (currentUserId != SessionManager.INVALID_USER_ID) {
            getFeed()
        } else {
            _error.value = "User not logged in. Cannot refresh feed."
            _feedItems.value = emptyList()
        }
    }

    fun clearError() {
        _error.value = null
    }
}
