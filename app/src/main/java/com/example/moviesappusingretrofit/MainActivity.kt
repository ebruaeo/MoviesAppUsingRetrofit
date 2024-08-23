package com.example.moviesappusingretrofit

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moviesappusingretrofit.adapter.MovieAdapter
import com.example.moviesappusingretrofit.api.ApiClient
import com.example.moviesappusingretrofit.api.ApiService
import com.example.moviesappusingretrofit.databinding.ActivityMainBinding
import com.example.moviesappusingretrofit.response.MovieListResponse
import retrofit2.Call
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val movieAdapter by lazy {
        MovieAdapter()
    }
    private val api: ApiService by lazy {
        ApiClient().getClient().create(ApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.apply {
            prgBarMovie.visibility = View.VISIBLE

            val callMovieApi = api.getPopularMovie(1)
            callMovieApi.enqueue(object : retrofit2.Callback<MovieListResponse> {
                override fun onResponse(
                    call: Call<MovieListResponse>,
                    response: Response<MovieListResponse>
                ) {
                    prgBarMovie.visibility = View.GONE
                    when (response.code()) {
                        // Successful response
                        in 200..299 -> {
                            response.body().let { itBody ->
                                itBody?.results.let { itData ->
                                    if (itData!!.isNotEmpty()) {
                                        movieAdapter.differ.submitList(itData)
                                        rvMovie.apply {
                                            layoutManager = LinearLayoutManager(this@MainActivity)
                                            adapter = movieAdapter
                                        }
                                    }
                                }
                            }
                        }
                        // Redirection Message
                        in 300..399 -> {
                            Log.d("Response Code", "Redirection messages : ${response.code()}")
                        }
                        // Client error responses
                        in 400..499 -> {
                            Log.d("Response Code", "Client Error responses : ${response.code()}")

                        }
                        // Server error responses
                        in 500..599 -> {
                            Log.d("Response Code", "Server error responses : ${response.code()}")

                        }
                    }
                }

                override fun onFailure(call: Call<MovieListResponse>, t: Throwable) {
                    binding.prgBarMovie.visibility = View.GONE
                    Log.e("onFailure", "Err: ${t.message}")
                }

            })
        }
    }
}