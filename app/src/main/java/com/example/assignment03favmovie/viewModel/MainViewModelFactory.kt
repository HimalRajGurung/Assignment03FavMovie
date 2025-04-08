package com.example.assignment03favmovie.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.assignment03favmovie.repository.Repository

class MainViewModelFactory(private val movieRepository: Repository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(com.example.assignment03favmovie.viewModel.MainViewModel::class.java)) {
            return MainViewModel(movieRepository) as T // Casting to RegisterViewModel
        }
        throw IllegalArgumentException("Unknown MainViewModel class")
    }
}
