package com.example.assignment03favmovie.ui.home.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignment03favmovie.databinding.FragmentHomeBinding
import com.example.assignment03favmovie.model.Movie
import com.example.assignment03favmovie.repository.Repository
import com.example.assignment03favmovie.ui.home.adpater.MovieAdapter
import com.example.assignment03favmovie.ui.home.movieDetails.MovieDetailsActivity
import com.example.assignment03favmovie.viewModel.MainViewModel
import com.example.assignment03favmovie.viewModel.MainViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment(), FragmentView {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var viewModel: MainViewModel
    private var allMovies: List<Movie> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val repo = Repository(FirebaseAuth.getInstance())
        viewModel = ViewModelProvider(
            requireActivity(),
            MainViewModelFactory(repo)
        )[MainViewModel::class.java]

        // Fetch movies from Firestorm
        viewModel.fetchMovies()
        setupRecyclerView()
        observeMovies()
        observeLoadingState()
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter(this)
        binding.movieRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = movieAdapter
        }
    }

    private fun observeMovies() {
        viewModel.movies.observe(viewLifecycleOwner) { movies ->
            allMovies = movies // Store all movies when data is fetched
            movieAdapter.submitList(movies)
            setupSearch() // Set up search functionality after data is loaded
        }
    }

    private fun setupSearch() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().lowercase()
                val filtered = allMovies.filter {
                    it.title.lowercase().contains(query) || it.studio.lowercase().contains(query)
                }
                movieAdapter.submitList(filtered)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun observeLoadingState() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.movieRecyclerView.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.movieRecyclerView.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchMovies()
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
            putExtra("source", "home")
        }
        startActivity(intent)
    }
}
