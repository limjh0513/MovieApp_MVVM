package com.example.moviemvvm.data.api

import com.example.moviemvvm.data.vo.MovieDetails
import com.example.moviemvvm.data.vo.MovieResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TheMovieDBInterface {
    //https://api.themoviedb.org/3/
    //https://api.themoviedb.org/3/movie/299534?api_key=e61f9252cf066864ddb585e0f4b1084a  details
    //https://api.themoviedb.org/3/movie/popular?api_key=e61f9252cf066864ddb585e0f4b1084a popular

    @GET("movie/{movie_id}")
    fun getMovieDetails(@Path("movie_id") id: Int): Single<MovieDetails>

    @GET("movie/popular")
    fun getPopularMovie(@Query("page") page: Int): Single<MovieResponse>
}