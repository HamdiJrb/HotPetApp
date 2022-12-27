package com.example.hotpet.api

import com.example.hotpet.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface ApiService {
    companion object {

        private var retrofit: Retrofit? = null
        var userService: UserService? = null
        var chatService: ChatService? = null
        var likeService: LikeService? = null

        fun userService(): UserService {
            checkRetrofitService()
            if (userService == null) userService = retrofit!!.create(UserService::class.java)
            return userService!!
        }

        fun chatService(): ChatService {
            checkRetrofitService()
            if (chatService == null) chatService = retrofit!!.create(ChatService::class.java)
            return chatService!!
        }

        fun likeService(): LikeService {
            checkRetrofitService()
            if (likeService == null) likeService = retrofit!!.create(LikeService::class.java)
            return likeService!!
        }

        private fun checkRetrofitService() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
        }

    }
}