package tn.esprit.smartpet.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import tn.esprit.smartpet.model.User


// 2.Création d’une interface qui regroupe les différents endpoints avec une méthode "create"
// qui crée une instance
interface ApiInterface {

    // L'entete de chaque fonction

    // Sign in
    @POST("/user/login")   // endpoint=2ème partie de l'URL
    fun login(@Body user: User):Call<User>

    // Sign up
    @POST("/User/signup")
    fun signup(@Body user: User):Call<User>


    // getProfileData
    @POST("/User/getProfileData")
    fun getProfileData(@Body user:User):Call<User>

    // 3. Builder + Converter
    companion object {

        fun create() : ApiInterface {

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://192.168.1.183:3000")
                .build()

            return retrofit.create(ApiInterface::class.java)
        }
    }
}