package com.example.hotpet.api.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hotpet.api.ApiService
import com.example.hotpet.api.UserService
import com.example.hotpet.api.models.User
import kotlinx.coroutines.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.net.SocketException

class UserViewModel : ViewModel() {

    val errorMessage = MutableLiveData<String>()
    val userList = MutableLiveData<List<User>>()
    val user = MutableLiveData<User>()
    val image = MutableLiveData<String>()
    val token = MutableLiveData<String>()
    val method = MutableLiveData<String>()
    private var job: Job? = null
    val loading = MutableLiveData<Boolean>()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    fun getAll() {
        loading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = ApiService.userService().getAll()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    userList.postValue(response.body())
                    loading.value = false
                } else {
                    onError(response.message())
                }
            }
        }
    }

    fun getById(userId: String) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = ApiService.userService().getById(userId)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    user.postValue(response.body())
                    loading.value = false
                } else {
                    onError(response.message())
                }
            }
        }
    }

    fun login(loginBody: UserService.LoginBody) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = ApiService.userService().login(loginBody)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    user.postValue(response.body())
                    loading.value = false
                } else {
                    if (response.code() == 403) {
                        onError("Invalid credentials")
                    } else if (response.code() == 402) {
                        onError("Email not verified")
                    } else {
                        onError(response.message())
                    }
                }
            }
        }
    }

    fun loginWithSocial(loginWithSocialBody: UserService.LoginWithSocialBody) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = ApiService.userService().loginWithSocial(loginWithSocialBody)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    user.postValue(response.body())
                    loading.value = false
                } else {
                    onError(response.message())
                }
            }
        }
    }

    fun register(userBody: UserService.RegisterBody) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = ApiService.userService().register(userBody)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    user.postValue(response.body())
                    loading.value = false
                } else {
                    if (response.code() == 403) {
                        onError("User already exist !")
                    } else {
                        onError(response.message())
                    }

                }
            }
        }
    }

    fun updateProfileImage(updateImageBody: UserService.UpdateImageBody) {
        val methodName = object {}.javaClass.enclosingMethod?.name!!
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = ApiService.userService().updateProfileImage(updateImageBody)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    image.postValue(response.body())
                    method.postValue(methodName)
                    loading.value = false
                } else {
                    onError(response.message())
                }
            }
        }
    }

    fun addImage(partMap: MutableMap<String, RequestBody>, file: MultipartBody.Part?) {
        val methodName = object {}.javaClass.enclosingMethod?.name!!
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = ApiService.userService().addImage(partMap, file)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    image.postValue(response.body())
                    method.postValue(methodName)
                    loading.value = false
                } else {
                    onError(response.message())
                }
            }
        }
    }

    fun deleteImage(deleteImageBody: UserService.DeleteImageBody) {
        val methodName = object {}.javaClass.enclosingMethod?.name!!
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = ApiService.userService().deleteImage(deleteImageBody)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    image.postValue(response.body())
                    method.postValue(methodName)
                    loading.value = false
                } else {
                    onError(response.message())
                }
            }
        }
    }

    fun forgotPassword(resetBody: UserService.ForgotPasswordBody) {
        val methodName = object {}.javaClass.enclosingMethod?.name!!
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = ApiService.userService().forgotPassword(resetBody)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    token.postValue(response.body())
                    method.postValue(methodName)
                    loading.value = false
                } else {
                    onError(response.message())
                }
            }
        }
    }

    fun verifyResetCode(verifyResetCodeBody: UserService.VerifyResetCodeBody) {
        val methodName = object {}.javaClass.enclosingMethod?.name!!
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = ApiService.userService().verifyResetCode(verifyResetCodeBody)
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

    fun updatePassword(updatePasswordBody: UserService.UpdatePasswordBody) {
        val methodName = object {}.javaClass.enclosingMethod?.name!!
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = ApiService.userService().updatePassword(updatePasswordBody)
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

    fun updatePreferredParams(updatePreferredAgeBody: UserService.UpdatePreferredParamsBody) {
        val methodName = object {}.javaClass.enclosingMethod?.name!!
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = ApiService.userService().updatePreferredParams(updatePreferredAgeBody)
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

    fun updateLocation(updateLocationBody: UserService.UpdateLocationBody) {
        val methodName = object {}.javaClass.enclosingMethod?.name!!
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = ApiService.userService().updateLocation(updateLocationBody)
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

    fun updateProfile(updateProfileBody: UserService.UpdateProfileBody) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = ApiService.userService().updateProfile(updateProfileBody)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    user.postValue(response.body())
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
