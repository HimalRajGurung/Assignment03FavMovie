package com.example.assignment03favmovie.ui.home.adpater


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.assignment03favmovie.R
import com.example.assignment03favmovie.databinding.ItemMovieBinding
import com.example.assignment03favmovie.model.Movie
import com.example.assignment03favmovie.ui.home.fragment.FragmentView

class MovieAdapter(
    private val fragmentView: FragmentView,
) : ListAdapter<Movie, MovieAdapter.MovieViewHolder>(MovieDiffCallback()) {


    inner class MovieViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            binding.movieTitle.text = movie.title
            binding.movieStudio.text = movie.studio
            binding.movieRating.text = "Rating: ${movie.criticsRating}/5"

            Glide.with(binding.root.context).load(movie.posterUrl).centerCrop()
                .error(R.drawable.movie_palceholder).into(binding.moviePoster)

            binding.root.setOnClickListener {
                fragmentView.goToDetails(movie)
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position))

    }
}

class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean =
        oldItem.movieId == newItem.movieId

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean = oldItem == newItem
}
