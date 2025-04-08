package com.example.assignment03favmovie.ui.home.addMovie

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.assignment03favmovie.databinding.ActivityAddMovieBinding
import com.example.assignment03favmovie.model.Movie
import com.example.assignment03favmovie.repository.Repository
import com.example.assignment03favmovie.viewModel.MainViewModel
import com.example.assignment03favmovie.viewModel.MainViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID


class AddMovieActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityAddMovieBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val repo = Repository(FirebaseAuth.getInstance())
        viewModel = ViewModelProvider(this, MainViewModelFactory(repo))[MainViewModel::class.java]
        viewModel.isLoading.observe(this, Observer { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.mainLayout.isEnabled = !isLoading
        })

        viewModel.movieAddedSuccess.observe(this, Observer { success ->
            if (success) {
                showSuccessDialog()
            }
        })

        viewModel.errorMessage.observe(this, Observer { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                showErrorDialog(errorMessage)
            }
        })

        binding.backButton.setOnClickListener {
            finish()
        }



        val movieId = UUID.randomUUID().toString()

        binding.btnAddMovie.setOnClickListener {
            val posterUrl = binding.editPosterUrl.text.toString()
            val title = binding.editTitle.text.toString()
            val studio = binding.editStudio.text.toString()
            val rating = binding.ratingCritics.rating

            if (posterUrl.isNotEmpty() && title.isNotEmpty() && studio.isNotEmpty()) {
                val movie = Movie(movieId,title, studio,posterUrl, rating.toString())
                viewModel.addMovie(movie)
            } else {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Success")
            .setMessage("Movie added successfully!")
            .setPositiveButton("OK") { _, _ ->
                finish()
            }
            .show()
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }


    private fun addDefaultMovies() {
        val additionalMovies = listOf(
            Movie(UUID.randomUUID().toString(), "Inception", "Warner Bros", "https://image.tmdb.org/t/p/w185/qmDpIHrmpJINaRKAfWQfftjCdyi.jpg", "4.5"),
            Movie(UUID.randomUUID().toString(), "The Dark Knight", "Warner Bros", "https://image.tmdb.org/t/p/w185/1hRoyzDtpgMU7Dz4JF22RANzQO7.jpg", "5.0"),
            Movie(UUID.randomUUID().toString(), "Interstellar", "Paramount", "https://image.tmdb.org/t/p/w185/rAiYTfKGqDCRIIqo664sY9XZIvQ.jpg", "4.7"),
            Movie(UUID.randomUUID().toString(), "Avengers: Endgame", "Marvel", "https://image.tmdb.org/t/p/w185/ulzhLuWrPK07P1YkdWQLZnQh1JL.jpg", "4.8"),
            Movie(UUID.randomUUID().toString(), "Titanic", "20th Century Fox", "https://image.tmdb.org/t/p/w185/kHXEpyfl6zqn8a6YuozZUujufXf.jpg", "4.6"),
            Movie(UUID.randomUUID().toString(), "Avatar", "20th Century Fox", "https://image.tmdb.org/t/p/w185/6EiRUJpuoeQPghrs3YNktfnqOVh.jpg", "4.5"),
            Movie(UUID.randomUUID().toString(), "Joker", "Warner Bros", "https://image.tmdb.org/t/p/w185/udDclJoHjfjb8Ekgsd4FDteOkCU.jpg", "4.4"),
            Movie(UUID.randomUUID().toString(), "The Matrix", "Warner Bros", "https://image.tmdb.org/t/p/w185/f89U3ADr1oiB1s9GkdPOEpXUk5H.jpg", "4.9"),
            Movie(UUID.randomUUID().toString(), "Frozen", "Disney", "https://image.tmdb.org/t/p/w185/xJWPZIYOEFIjZpBL7SVBGnzRYXp.jpg", "4.0"),
            Movie(UUID.randomUUID().toString(), "Moana", "Disney", "https://image.tmdb.org/t/p/w185/4JeeEDkY7WyDtw5eU5lphqdDg5i.jpg", "4.3"),
            Movie(UUID.randomUUID().toString(), "The Lion King", "Disney", "https://image.tmdb.org/t/p/w185/2bXbqYdUdNVa8VIWXVfclP2ICtT.jpg", "4.8"),
            Movie(UUID.randomUUID().toString(), "Toy Story", "Pixar", "https://image.tmdb.org/t/p/w185/uXDfjJbdP4ijW5hWSBrPrlKpxab.jpg", "4.7"),
            Movie(UUID.randomUUID().toString(), "Finding Nemo", "Pixar", "https://image.tmdb.org/t/p/w185/eHuGQ10FUzK1mdOY69wF5pGgEf5.jpg", "4.6"),
            Movie(UUID.randomUUID().toString(), "Shrek", "DreamWorks", "https://image.tmdb.org/t/p/w185/iB64vpL3dIObOtMZgX3RqdVdQDc.jpg", "4.5"),
            Movie(UUID.randomUUID().toString(), "Spider-Man: No Way Home", "Marvel", "https://image.tmdb.org/t/p/w185/1g0dhYtq4irTY1GPXvft6k4YLjm.jpg", "4.9"),
            Movie(UUID.randomUUID().toString(), "Doctor Strange", "Marvel", "https://image.tmdb.org/t/p/w185/uGBVj3bEbCoZbDjjl9wTxcygko1.jpg", "4.4"),
            Movie(UUID.randomUUID().toString(), "Black Panther", "Marvel", "https://image.tmdb.org/t/p/w185/uxzzxijgPIY7slzFvMotPv8wjKA.jpg", "4.5"),
            Movie(UUID.randomUUID().toString(), "The Batman", "Warner Bros", "https://image.tmdb.org/t/p/w185/74xTEgt7R36Fpooo50r9T25onhq.jpg", "4.3"),
            Movie(UUID.randomUUID().toString(), "Dune", "Warner Bros", "https://image.tmdb.org/t/p/w185/d5NXSklXo0qyIYkgV94XAgMIckC.jpg", "4.2"),
            Movie(UUID.randomUUID().toString(), "Everything Everywhere All at Once", "A24", "https://image.tmdb.org/t/p/w185/w3LxiVYdWWRvEVdn5RYq6jIqkb1.jpg", "4.6"),
            Movie(UUID.randomUUID().toString(), "The Greatest Showman", "20th Century Fox", "https://image.tmdb.org/t/p/w185/h36w0zZP2gEOA2A5gO9wly6Xz0y.jpg", "4.5"),
            Movie(UUID.randomUUID().toString(), "Us", "Universal Pictures", "https://image.tmdb.org/t/p/w185/ux2dU1jQ2ACIMShzB3yP93Udpzc.jpg", "4.0"),
            Movie(UUID.randomUUID().toString(), "65", "Sony Pictures", "https://image.tmdb.org/t/p/w185/rzRb63TldOKdKydCvWJM8B6EkPM.jpg", "3.8"),
            Movie(UUID.randomUUID().toString(), "Here", "Independent", "https://image.tmdb.org/t/p/w185/your_poster_path.jpg", "4.1"),
            Movie(UUID.randomUUID().toString(), "The Super Mario Bros. Movie", "Universal Pictures", "https://image.tmdb.org/t/p/w185/qNBAXBIQlnOThrVvA6mA2B5ggV6.jpg", "4.3"),
            Movie(UUID.randomUUID().toString(), "Twisters", "Universal Pictures", "https://image.tmdb.org/t/p/w185/your_poster_path.jpg", "4.3"),
            Movie(UUID.randomUUID().toString(), "Inside Out 2", "Disney", "https://image.tmdb.org/t/p/w185/your_poster_path.jpg", "4.9")
        )

        for (movie in additionalMovies) {
            viewModel.addMovie(movie)
        }



    }
}
