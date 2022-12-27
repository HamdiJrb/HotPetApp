package com.example.hotpet.api.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hotpet.api.ApiService
import com.example.hotpet.api.ChatService
import com.example.hotpet.api.models.Conversation
import com.example.hotpet.api.models.Message
import kotlinx.coroutines.*
import java.net.SocketException

class ChatViewModel : ViewModel() {

    val errorMessage = MutableLiveData<String>()
    val messageList = MutableLiveData<List<Message>>()
    val conversationList = MutableLiveData<List<Conversation>>()
    val conversation = MutableLiveData<Conversation>()
    val message = MutableLiveData<Message>()
    val state = MutableLiveData<Boolean>()
    private var job: Job? = null
    val loading = MutableLiveData<Boolean>()
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    fun getMyConversations(senderId: String) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = ApiService.chatService().getMyConversations(senderId)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    conversationList.postValue(response.body())
                    loading.value = false
                } else {
                    onError(response.message())
                }
            }
        }
    }

    fun getMyMessages(conversationId: String) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = ApiService.chatService().getMyMessages(conversationId)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    messageList.postValue(response.body())
                    loading.value = false
                } else {
                    onError(response.message())
                }
            }
        }
    }

    fun createNewConversation(conversationBody: ChatService.ConversationBody) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = ApiService.chatService().createNewConversation(conversationBody)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    conversation.postValue(response.body())
                    loading.value = false
                } else {
                    onError(response.message())
                }
            }
        }
    }

    fun sendMessage(messageBody: ChatService.MessageBody) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = ApiService.chatService().sendMessage(messageBody)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    state.value = true
                    loading.value = false
                } else {
                    onError(response.message())
                }
            }
        }
    }

    fun deleteConversation(id: String?) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = ApiService.chatService().deleteConversation(id)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    state.value = true
                    loading.value = false
                } else {
                    onError(response.message())
                }
            }
        }
    }

    private fun onError(message: String) {
        try {
            println("HTTP ERROR")
            errorMessage.postValue(message)
            loading.postValue(false)
        } catch (e: IllegalStateException) {
            println("NO INTERNET")
        } catch (e: SocketException) {
            println("SERVER CRASH")
        }
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

}
