package com.example.assignment03favmovie.model

data class Movie(
    val movieId: String = "",
    val title: String = "",
    val studio: String = "",
    val posterUrl: String = "",
    val criticsRating: String = "",
    val description: String = "",
    val favorite: Boolean = false
)
