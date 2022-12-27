package com.example.hotpet.api

import com.example.hotpet.api.models.Like
import com.example.hotpet.utils.DefaultResponse
import retrofit2.Response
import retrofit2.http.*

interface LikeService {

    // REQUEST

    data class LikeBody(val likedId: String, val likerId: String, val isRight: Boolean)

    // ROUTES

    @POST("/like")
    suspend fun add(@Body likeBody: LikeBody): Response<Like>

    @GET("/like/get-my/{userId}")
    suspend fun getMy(@Path("userId") userId: String): Response<List<Like>>

    @DELETE("/like/one/{id}")
    suspend fun delete(@Path("id") id: String): Response<DefaultResponse>

}