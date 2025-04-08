package com.example.assignment03favmovie.ui.home.movieDetails

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.assignment03favmovie.R
import com.example.assignment03favmovie.databinding.ActivityMovieDetailsBinding
import com.example.assignment03favmovie.model.Movie
import com.example.assignment03favmovie.repository.Repository
import com.example.assignment03favmovie.viewModel.MainViewModel
import com.example.assignment03favmovie.viewModel.MainViewModelFactory
import com.google.firebase.auth.FirebaseAuth



class MovieDetailsActivity : AppCompatActivity() {
    val TAG = "MovieDetailsActivity"
    private lateinit var binding: ActivityMovieDetailsBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var movie: Movie

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repo = Repository(FirebaseAuth.getInstance())
        viewModel = ViewModelProvider(this, MainViewModelFactory(repo))[MainViewModel::class.java]

        val movieId = intent.getStringExtra("movieId") ?: ""
        val title = intent.getStringExtra("title") ?: ""
        val studio = intent.getStringExtra("studio") ?: ""
        val posterUrl = intent.getStringExtra("posterUrl") ?: ""
        val criticsRating = intent.getStringExtra("criticsRating") ?: ""
        val isFavorite = intent.getBooleanExtra("isFavorite", false)
        val description = intent.getStringExtra("description") ?: ""
        val source = intent.getStringExtra("source") ?: "home"
        if (source == "favorites"){binding.tvTitle.text ="Favorites Movie Details"}

            movie = Movie(
            movieId = movieId,
            title = title,
            studio = studio,
            posterUrl = posterUrl,
            criticsRating = criticsRating,
            favorite = isFavorite,
            description = description
        )

        updateUI(movie)
        observeLoading()

        if (source == "favorites") {
            binding.etDescription.visibility = View.VISIBLE
            binding.btDelete.visibility = View.VISIBLE
            binding.btUpdate.visibility = View.VISIBLE
            binding.etDescription.setText(movie.description)
            binding.tvDescription.visibility = View.GONE
        } else {
            binding.etDescription.visibility = View.GONE
            binding.btDelete.visibility = View.GONE

            binding.btUpdate.visibility = View.GONE
            binding.tvDescription.visibility = View.VISIBLE
            binding.tvDescription.text =  movie.description
        }

        binding.fabSave.setOnClickListener {
            markAsFavorite(movie)
        }

        binding.backButton.setOnClickListener {
            finish()
        }

        // Update Button Click
        binding.btUpdate.setOnClickListener {
            updateMovieDescription(movie)
        }

        // Delete Button Click
        binding.btDelete.setOnClickListener {
            deleteMovie(movie)
        }
    }

    private fun updateUI(movie: Movie) {
        binding.movieTitle.text = movie.title
        binding.movieStudio.text = movie.studio
        binding.movieRating.text = "Rating: ${movie.criticsRating}"

        Glide.with(this)
            .load(movie.posterUrl)
            .error(R.drawable.movie_palceholder)
            .into(binding.moviePoster)
    }

    private fun observeLoading() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.fabSave.isEnabled = !isLoading
            binding.fabSave.alpha = if (isLoading) 0.5f else 1.0f
            binding.btUpdate.isEnabled = !isLoading
            binding.btDelete.isEnabled = !isLoading
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun markAsFavorite(movie: Movie) {
        val updatedMovie = movie.copy(favorite = true)
        viewModel.updateMovieFavoriteStatus(updatedMovie)
        Toast.makeText(this, "Added to favorite!", Toast.LENGTH_SHORT).show()
    }

    private fun updateMovieDescription(movie: Movie) {
        val description = binding.etDescription.text.toString()
        val updatedMovie = movie.copy(description = description)
        viewModel.updateMovie(updatedMovie)
        Toast.makeText(this, "Movie updated!", Toast.LENGTH_SHORT).show()
    }

    private fun deleteMovie(movie: Movie) {
        viewModel.deleteMovie(movie)
        Toast.makeText(this, "Movie deleted!", Toast.LENGTH_SHORT).show()
        finish() // Close the activity after delete
    }
}
