package com.example.moviesappusingretrofit

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.size.Scale
import com.example.moviesappusingretrofit.api.ApiClient
import com.example.moviesappusingretrofit.api.ApiService
import com.example.moviesappusingretrofit.databinding.ActivityDetailsMovieBinding
import com.example.moviesappusingretrofit.databinding.ItemRowBinding
import com.example.moviesappusingretrofit.response.DetailsMovieResponse
import com.example.moviesappusingretrofit.utils.Constants.POSTER_BASEURL
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailsMovieActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsMovieBinding
    private val api: ApiService by lazy {
        ApiClient().getClient().create(ApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailsMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val movieId = intent.getIntExtra("id", 1)
        binding.apply {
            val callDetailsMovieApi = api.getMovieDetails(movieId)
            callDetailsMovieApi.enqueue(object : Callback<DetailsMovieResponse> {
                override fun onResponse(
                    call: Call<DetailsMovieResponse>,
                    response: Response<DetailsMovieResponse>
                ) {
                    when (response.code()) {
                        // Successful response
                        in 200..299 -> {
                            response.body().let { itBody ->
                                val imagePoster = POSTER_BASEURL + itBody!!.poster_path
                                imgMovie.load(imagePoster) {
                                    crossfade(true)
                                    placeholder(R.drawable.poster_placeholder)
                                    scale(Scale.FILL)
                                }
                                imgBackground.load(imagePoster) {
                                    crossfade(true)
                                    placeholder(R.drawable.poster_placeholder)
                                    scale(Scale.FILL)
                                }
                                prgBarMovies.visibility = View.GONE

                                tvMovieName.text = itBody.title
                                tvMovieTagLine.text = itBody.tagline
                                tvMovieDateRelease.text = itBody.release_date
                                tvMovieRating.text = itBody.vote_average.toString()
                                tvMovieRuntime.text = itBody.runtime.toString()
                                tvMovieBudget.text = itBody.budget.toString()
                                tvMovieRevenue.text = itBody.revenue.toString()
                                tvMovieOverview.text = itBody.overview
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

                override fun onFailure(call: Call<DetailsMovieResponse>, t: Throwable) {
                    Log.e("onFailure", "Err: ${t.message}")

                }

            })
        }
    }
}