package com.example.assignment03favmovie.ui.home.fragment


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignment03favmovie.databinding.FragmentFavoritesBinding
import com.example.assignment03favmovie.model.Movie
import com.example.assignment03favmovie.repository.Repository
import com.example.assignment03favmovie.ui.home.adpater.MovieAdapter
import com.example.assignment03favmovie.ui.home.movieDetails.MovieDetailsActivity
import com.example.assignment03favmovie.viewModel.MainViewModel
import com.example.assignment03favmovie.viewModel.MainViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class FavoritesFragment : Fragment(), FragmentView {
    val TAG  = "FavoritesFragment"
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val repo = Repository(FirebaseAuth.getInstance())
        viewModel = ViewModelProvider(
            requireActivity(),
            MainViewModelFactory(repo)
        )[MainViewModel::class.java]

        viewModel.fetchMovies()
        setupRecyclerView()
        observeMovies()
        observeLoadingState()

    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchMovies()
    }


    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter(this)
        binding.favoritesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = movieAdapter
        }
    }

    private fun observeMovies() {
        viewModel.movies.observe(viewLifecycleOwner) { movies ->
            Log.d(TAG, "actual movies: "+ movies.count())
            val favoriteMovies = movies.filter { it.favorite }
            movieAdapter.submitList(favoriteMovies)
            Log.d(TAG, "observeMovies: "+ favoriteMovies.count()+ " "+ favoriteMovies)
        }
    }

    private fun observeLoadingState() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.favoritesRecyclerView.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.favoritesRecyclerView.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun goToDetails(movie: Movie) {
        val intent = Intent(requireContext(), MovieDetailsActivity::class.java).apply {
            putExtra("movieId", movie.movieId)
            putExtra("title", movie.title)
            putExtra("studio", movie.studio)
            putExtra("posterUrl", movie.posterUrl)
            putExtra("criticsRating", movie.criticsRating)
            putExtra("isFavorite", movie.favorite)
            putExtra("description", movie.description)
            putExtra("source", "favorites")
        }
        startActivity(intent)
    }
}
