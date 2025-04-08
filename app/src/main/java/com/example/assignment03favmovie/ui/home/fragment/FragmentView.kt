package com.example.assignment03favmovie.ui.home.fragment

import com.example.assignment03favmovie.model.Movie

interface FragmentView {
     fun goToDetails(movie: Movie)
}