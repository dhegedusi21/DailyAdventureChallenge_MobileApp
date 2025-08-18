package com.example.dailyadventurechallenge

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.dailyadventurechallenge.data.api.RetrofitClient
import com.example.dailyadventurechallenge.data.repository.AuthRepository
import com.example.dailyadventurechallenge.data.session.SessionManager
import com.example.dailyadventurechallenge.ui.login.LoginViewModel
import com.example.dailyadventurechallenge.ui.login.LoginViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var errorText: TextView

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sessionManager = SessionManager(this)

        if (sessionManager.isLoggedIn()) {
            navigateToMainActivity()
            return
        }

        val repository = AuthRepository(RetrofitClient.apiService)
        val viewModelFactory = LoginViewModelFactory(application, repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]

        emailEditText = findViewById(R.id.etEmail)
        passwordEditText = findViewById(R.id.etPassword)
        loginButton = findViewById(R.id.btnLogin)
        progressBar = findViewById(R.id.progressBar)
        errorText = findViewById(R.id.tvError)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (validateInput(email, password)) {
                performLogin(email, password)
            }
        }

        viewModel.loginResult.observe(this) { result ->
            result?.let { customResult ->
                when (customResult) {
                    is com.example.dailyadventurechallenge.data.repository.Result.Success -> {
                        val authResponse = customResult.data
                        if (authResponse.isSuccess) {
                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                            navigateToMainActivity()
                        } else {
                            val errorMessage = authResponse.message ?: "Login failed. Please check your credentials."
                            errorText.visibility = View.VISIBLE
                            errorText.text = errorMessage
                        }
                    }
                    is com.example.dailyadventurechallenge.data.repository.Result.Error -> {
                        val errorMessage = customResult.exception.message ?: "Login failed. An unexpected error occurred."
                        errorText.visibility = View.VISIBLE
                        errorText.text = errorMessage
                    }
                }
            }
        }


        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                progressBar.visibility = View.VISIBLE
                loginButton.isEnabled = false
            } else {
                progressBar.visibility = View.GONE
                loginButton.isEnabled = true
            }
        }

        findViewById<TextView>(R.id.tvForgotPassword).setOnClickListener {
            Toast.makeText(this, "Forgot password clicked", Toast.LENGTH_SHORT).show()
        }

        findViewById<TextView>(R.id.tvSignUp).setOnClickListener {
            Toast.makeText(this, "Sign up clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            emailEditText.error = "Email is required"
            return false
        }

        if (password.isEmpty()) {
            passwordEditText.error = "Password is required"
            return false
        }

        return true
    }

    private fun performLogin(email: String, password: String) {
        errorText.visibility = View.GONE
        viewModel.login(email, password)
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
