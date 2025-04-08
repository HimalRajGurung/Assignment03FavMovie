package com.example.assignment03favmovie.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.assignment03favmovie.databinding.ActivityLoginBinding
import com.example.assignment03favmovie.repository.Repository
import com.example.assignment03favmovie.ui.home.MainActivity
import com.example.assignment03favmovie.viewModel.MainViewModel
import com.example.assignment03favmovie.viewModel.MainViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repo = Repository(FirebaseAuth.getInstance())
        viewModel = ViewModelProvider(this, MainViewModelFactory(repo))[MainViewModel::class.java]

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.loginUser(email, password)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isLoading.observe(this) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(this) { message ->
            binding.errorMessage.apply {
                visibility = View.VISIBLE
                text = message
            }
        }

        viewModel.navigateToHome.observe(this) {
            if (it == true) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }
}
