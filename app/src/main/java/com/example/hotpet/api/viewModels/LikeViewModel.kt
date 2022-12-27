package com.example.hotpet.api.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hotpet.api.ApiService
import com.example.hotpet.api.LikeService
import com.example.hotpet.api.models.Like
import kotlinx.coroutines.*
import java.net.SocketException

class LikeViewModel : ViewModel() {

    val errorMessage = MutableLiveData<String>()
    val likeList = MutableLiveData<List<Like>>()
    val like = MutableLiveData<Like>()
    val method = MutableLiveData<String>()
    private var job: Job? = null
    val loading = MutableLiveData<Boolean>()
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    fun add(likeBody: LikeService.LikeBody) {
        val methodName = object {}.javaClass.enclosingMethod?.name!!
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = ApiService.likeService().add(likeBody)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    method.postValue(methodName)
                    like.postValue(response.body())
                    loading.value = false
                } else {
                    onError(response.message())
                }
            }
        }
    }

    fun getMy(userId: String) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = ApiService.likeService().getMy(userId)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    likeList.postValue(response.body())
                    loading.value = false
                } else {
                    onError(response.message())
                }
            }
        }
    }

    fun delete(id: String) {
        val methodName = object {}.javaClass.enclosingMethod?.name!!
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = ApiService.likeService().delete(id)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    method.postValue(methodName)
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
            e.printStackTrace()
        } catch (e: SocketException) {
            println("SERVER CRASH")
        }
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

}
