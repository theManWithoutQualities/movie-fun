package com.example.moviefun

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

interface MyApi {
    @GET("/api/get")
    fun getPopularMovie(): Deferred<Response<List<PostModel>>>
}

class MainActivity : AppCompatActivity(), CoroutineScope {

    lateinit var client: OkHttpClient
    lateinit var retrofit: Retrofit
    lateinit var api: MyApi
    private lateinit var mJob: Job
    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        client = OkHttpClient()
        retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl("https://umorili.herokuapp.com")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
        api = retrofit.create(MyApi::class.java)
        mJob = Job()
        launch(Dispatchers.Default) {
            try {
                val popularMovies = api.getPopularMovie().await().body()
                Log.d("result", popularMovies.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }
}
