package com.example.moviesappusingretrofit.api

import retrofit2.Call
import com.example.moviesappusingretrofit.response.MovieListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("movie/popular")
    fun getPopularMovie(@Query("page") page : Int) : Call<MovieListResponse>

}