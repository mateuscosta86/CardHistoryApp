package br.com.codingwithmatts.conductor.model

import br.com.codingwithmatts.conductor.model.History
import br.com.codingwithmatts.conductor.model.Resume
import br.com.codingwithmatts.conductor.model.Usage
import br.com.codingwithmatts.conductor.model.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PostManApi {

    @GET("card-statement")
    fun getPurchases(@Query("month") month : Int,
                     @Query("year") year : Int,
                     @Query("page") page : String) : Call<History>

    @GET("resume")
    fun getResume() : Call<Resume>

    @GET("users/profile")
    fun getProfile() : Call<User>

    @GET("card-usage")
    fun getCardUsage() : Call<List<Usage>>
}