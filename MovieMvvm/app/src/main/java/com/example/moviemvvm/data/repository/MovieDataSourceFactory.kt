package com.example.moviemvvm.data.repository

import androidx.lifecycle.MutableLiveData
import com.example.moviemvvm.data.api.TheMovieDBInterface
import com.example.moviemvvm.data.vo.Movie
import androidx.paging.DataSource
import io.reactivex.disposables.CompositeDisposable

class MovieDataSourceFactory(private val apiService: TheMovieDBInterface, private val compositeDisposable: CompositeDisposable): DataSource.Factory<Int, Movie>() {

    val moviesLivedataSource = MutableLiveData<MovieDataSource>()

    override fun create(): DataSource<Int, Movie> {
        val movieDataSource = MovieDataSource(apiService, compositeDisposable)

        moviesLivedataSource.postValue(movieDataSource)
        return movieDataSource
    }

}