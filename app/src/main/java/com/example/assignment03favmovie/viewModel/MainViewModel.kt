package com.example.assignment03favmovie.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment03favmovie.model.Movie
import com.example.assignment03favmovie.model.MovieCallback
import com.example.assignment03favmovie.repository.Repository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch


class MainViewModel(private val repo: Repository) : ViewModel() {

    private val _registrationResult = MutableLiveData<Result<FirebaseUser?>>()
    val registrationResult: LiveData<Result<FirebaseUser?>> = _registrationResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _navigateToHome = MutableLiveData<Boolean>()
    val navigateToHome: LiveData<Boolean> get() = _navigateToHome

    private val _movies = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>> get() = _movies


    private val _movieAddedSuccess = MutableLiveData<Boolean>()
    val movieAddedSuccess: LiveData<Boolean> get() = _movieAddedSuccess

    fun register(email: String, password: String) {
        _isLoading.value = true

        repo.registerUser(email, password).observeForever { result ->
            _isLoading.value = false
            _registrationResult.value = result

            if (result.isFailure) {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Registration failed"
            } else {
                _navigateToHome.value = true
            }
        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repo.loginUser(email, password)

            _isLoading.value = false
            if (result.isSuccess) {
                _navigateToHome.value = true
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Login failed"
            }
        }
    }


    fun addMovie(movie: Movie) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repo.addMovieToFireStore(movie)

            _isLoading.value = false
            if (result.isSuccess) {
                _movieAddedSuccess.value = true
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Error adding movie"
            }
        }
    }

    fun fetchMovies() {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repo.getMovies()
            _isLoading.value = false
            if (result.isSuccess) {
                _movies.value = result.getOrNull()?: emptyList()
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Error fetching movies"
            }
        }
    }

    fun updateMovieFavoriteStatus(movie: Movie) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repo.updateMovie(movie)
            _isLoading.value = false
            if (result.isFailure) {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Error updating movie"
            }
        }
    }

    fun updateMovie(movie: Movie) {
        _isLoading.value = true
        repo.updateMovie(movie, object : MovieCallback {
            override fun onSuccess() {
                _isLoading.value = false
                fetchMovies()
            }

            override fun onFailure(exception: Exception) {
                _isLoading.value = false

            }
        })
    }

    fun deleteMovie(movie: Movie) {
        _isLoading.value = true
        repo.deleteMovie(movie, object : MovieCallback {
            override fun onSuccess() {
                _isLoading.value = false
                fetchMovies()
            }

            override fun onFailure(exception: Exception) {
                _isLoading.value = false

            }
        })
    }


}
