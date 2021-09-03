package com.mredrock.cyxbs.mine.page.feedback.network

import com.google.gson.annotations.SerializedName
import com.mredrock.cyxbs.mine.page.feedback.network.bean.ApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.io.Serializable

object TestRetrofit {

    private var mToken =
        "eyJEYXRhIjp7ImdlbmRlciI6IueUtyIsInN0dV9udW0iOiIyMDIwMjEyMzg5In0sIkRvbWFpbiI6Im1hZ2lwb2tlIiwiUmVkaWQiOiIwOWI1ZWE1NjZkOGIzZTM5MzQ2NmI0ZDBhZTA4ODQ0NmVmNjRmZDlmIiwiZXhwIjoiNzQwMDI5NDk4OSIsImlhdCI6IjE2MzA0ODM3MzUiLCJzdWIiOiJ3ZWIifQ==.svWnkzOCqWQosHZNCCGNr7SP7QgAWR07ti0NeXLzJ10K6Bo7ujRs+0no2jP2JpwDFF66REtZCPLJ3SzaFOcm6i1N+gsmb3DFS5iVcjrEi+NZpxENWzL2p+bLcB/BS20B2XCulqFBI9lhTzlHcR9HIp5s8e8V+W34ymvWDHb4C7yOqc4DFtZowSmY852Q9HqmdlfPbk1h04NmkOMyC6tjIeiffxePUYBQ6+l9MIBRa05uSk4Abbw800JhUrxwrYHXfu34Sg0swl5Vap7AwBKE7mmeN1QnnUjAUubWF+M6KzgC3H2RdsMvi9vx4XoQLGH3eqvca4Kyfh+iW7VjbIwN4Q=="
    private val logger =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    private val client = OkHttpClient.Builder()
        .addInterceptor(logger)
        .addInterceptor(Retry(1))
        .build()
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://be-prod.redrock.cqupt.edu.cn/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

    val testRetrofit = provideRetrofit()

    private fun provideRetrofit(): ApiService {
        return retrofit.build().create(ApiService::class.java)
    }


    class Retry(
        private val maxRetry: Int,
    ) : Interceptor {

        private var retryNum = 0

        override fun intercept(chain: Interceptor.Chain): Response {
            if (mToken.isEmpty()) {
                getNewToken()
            }
            val request = chain.request()
            val build = request
                .newBuilder()
                .addHeader(
                    "Authorization",
                    "Bearer $mToken"
                )
                .build()
            var response = chain.proceed(build)
            while (!response.isSuccessful && retryNum < maxRetry) {
                retryNum++
                getNewToken()
                val build2 = request
                    .newBuilder()
                    .addHeader(
                        "Authorization",
                        "Bearer $mToken"
                    )
                    .build()
                response.close()
                response = chain.proceed(build2)
            }
            retryNum = 0
            return response
        }
    }

    private fun getNewToken() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://be-dev.redrock.cqupt.edu.cn")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(TokenApiService::class.java)
        val call = api.getToken(TokenBody("2020212389", "669571"))
        try {
            val response = call.execute()
            if (response.isSuccessful) {
                val token = response.body()?.data?.token
                if (token != null) {
                    mToken = token
                }
            }
        } catch (e: Exception) {
        }
    }

    data class Token(
        @SerializedName("data")
        val `data`: Data,
        @SerializedName("status")
        val status: String,
    ) : Serializable {
        data class Data(
            @SerializedName("refreshToken")
            val refreshToken: String,
            @SerializedName("token")
            val token: String,
        ) : Serializable
    }

    data class TokenBody(
        @SerializedName("stuNum")
        val stuNum: String,
        @SerializedName("idNum")
        val idNum: String,
    ) : Serializable

    interface TokenApiService {
        @POST("/magipoke/token")
        fun getToken(
            @Body
            tokenBody: TokenBody,
        ): Call<Token>
    }
}

