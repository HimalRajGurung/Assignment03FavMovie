package com.example.assignment03favmovie.model

interface MovieCallback {
    fun onSuccess()
    fun onFailure(exception: Exception)
}