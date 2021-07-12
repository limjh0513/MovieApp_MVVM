package com.example.moviemvvm.ui.single_movie_details

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.moviemvvm.R
import com.example.moviemvvm.data.api.POSTER_BASE_URL
import com.example.moviemvvm.data.api.TheMovieDBClient
import com.example.moviemvvm.data.api.TheMovieDBInterface
import com.example.moviemvvm.data.repository.NetworkState
import com.example.moviemvvm.data.vo.MovieDetails
import com.example.moviemvvm.databinding.ActivitySingleMovieBinding
import java.text.NumberFormat
import java.util.*

class SingleMovie : AppCompatActivity() {
    private lateinit var viewModel: SingleMovieViewModel
    private lateinit var movieRepository: MovieDetailsRepository
    private lateinit var binding: ActivitySingleMovieBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_single_movie)
        binding.activity = this

        // val movieId: Int = 1
        val movieId: Int = intent.getIntExtra("id",1)
        val apiService: TheMovieDBInterface = TheMovieDBClient.getClient()
        movieRepository = MovieDetailsRepository(apiService)

        viewModel = getViewModel(movieId)
        viewModel.movieDetails.observe(this, Observer {
            bindUI(it)
        })

        viewModel.networkState.observe(this, Observer {
            binding.progressBar.visibility =
                if (it == NetworkState.LOADING) View.VISIBLE else View.GONE
            binding.txtError.visibility =
                if(it == NetworkState.ERROR) View.VISIBLE else View.GONE
        })
    }

    fun bindUI(it: MovieDetails?) {
        binding.movieTitle.text = it?.title
        binding.movieTagline.text = it?.tagline
        binding.movieReleaseDate.text = it?.releaseDate
        binding.movieRating.text = it?.rating.toString()
        binding.movieRuntime.text = it?.runtime.toString() + "minutes"
        binding.movieOverview.text = it?.overview

        val formatCurrency = NumberFormat.getCurrencyInstance(Locale.US)
        binding.movieBudget.text = formatCurrency.format(it?.budget)
        binding.movieRevenue.text = formatCurrency.format(it?.revenue)

        val moviePosterUrl = POSTER_BASE_URL + it?.posterPath
        Glide.with(this)
            .load(moviePosterUrl)
            .into(binding.ivMoviePoster)
    }

    private fun getViewModel(movieId: Int): SingleMovieViewModel {

        return ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return SingleMovieViewModel(movieRepository, movieId) as T
            }

        })[SingleMovieViewModel::class.java]
    }
}