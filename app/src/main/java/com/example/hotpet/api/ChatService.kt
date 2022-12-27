package com.example.hotpet.api

import com.example.hotpet.api.models.Conversation
import com.example.hotpet.api.models.Message
import com.example.hotpet.utils.DefaultResponse
import retrofit2.Response
import retrofit2.http.*

interface ChatService {

    // REQUEST

    data class ConversationBody(val sender: String, val receiver: String)

    data class MessageBody(
        val description: String,
        val senderId: String,
        val receiverId: String
    )

    // ROUTES

    @GET("/chat/my-conversations/{senderId}")
    suspend fun getMyConversations(@Path("senderId") senderId: String): Response<List<Conversation>>

    @GET("/chat/my-messages/{conversationId}")
    suspend fun getMyMessages(@Path("conversationId") conversationId: String): Response<List<Message>>

    @POST("/chat/add-conversation")
    suspend fun createNewConversation(@Body conversationBody: ConversationBody): Response<Conversation>

    @POST("/chat/send-message")
    suspend fun sendMessage(@Body messageBody: MessageBody): Response<DefaultResponse>

    @DELETE("/chat/one/{id}")
    suspend fun deleteConversation(@Path("id") id: String?): Response<DefaultResponse>
}