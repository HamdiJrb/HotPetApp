package com.example.hotpet.api

import com.example.hotpet.api.models.User
import com.example.hotpet.utils.Category
import com.example.hotpet.utils.DefaultResponse
import com.example.hotpet.utils.Gender
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import java.util.*

interface UserService {

    // REQUEST

    data class LoginBody(val email: String, val password: String)

    data class LoginWithSocialBody(val email: String)

    data class RegisterBody(
        val email: String,
        val password: String,
        val username: String,
        val birthdate: Date,
        val gender: Gender,
        val category: Category,
        val about: String,
    )

    data class UpdateProfileBody(
        val email: String,
        val username: String,
        val birthdate: Date,
        val gender: Gender,
        val category: Category,
        val about: String,
    )

    data class UpdateLocationBody(
        val email: String,
        val latitude: Double,
        val longitude: Double,
    )

    data class UpdatePreferredParamsBody(
        val email: String,
        val preferredAgeMin: Int,
        val preferredAgeMax: Int,
        val preferredDistance: Int,
    )

    data class DeleteImageBody(val email: String, val imageFilename: String)

    data class UpdateImageBody(val email: String, val imageFilename: String)

    data class ForgotPasswordBody(val email: String)

    data class VerifyResetCodeBody(val typedResetCode: String, val token: String)

    data class UpdatePasswordBody(val email: String, val password: String)

    // ROUTES

    @GET("/user")
    suspend fun getAll(): Response<List<User>>

    @GET("/user/one/{userId}")
    suspend fun getById(@Path("userId") userId: String): Response<User>

    @POST("/user/login")
    suspend fun login(@Body loginBody: LoginBody): Response<User>

    @POST("/user/login-with-social")
    suspend fun loginWithSocial(@Body loginWithSocialBody: LoginWithSocialBody): Response<User>

    @POST("/user/register")
    suspend fun register(@Body userBody: RegisterBody): Response<User>

    @POST("/user/forgot-password")
    suspend fun forgotPassword(@Body forgotPasswordBody: ForgotPasswordBody): Response<String>

    @POST("/user/verify-reset-code")
    suspend fun verifyResetCode(@Body verifyResetCodeBody: VerifyResetCodeBody): Response<DefaultResponse>

    @PUT("/user/update-password")
    suspend fun updatePassword(@Body updatePasswordBody: UpdatePasswordBody): Response<DefaultResponse>

    @PUT("/user/update-profile-image")
    suspend fun updateProfileImage(@Body updateImageBody: UpdateImageBody): Response<String>

    @Multipart
    @PUT("/user/add-image")
    suspend fun addImage(
        @PartMap partMap: MutableMap<String, RequestBody>,
        @Part file: MultipartBody.Part?
    ): Response<String>

    @PUT("/user/delete-image")
    suspend fun deleteImage(@Body deleteImageBody: DeleteImageBody): Response<String>

    @PUT("/user/update-preferred-params")
    suspend fun updatePreferredParams(@Body updatePreferredParams: UpdatePreferredParamsBody): Response<DefaultResponse>

    @PUT("/user/update-location")
    suspend fun updateLocation(@Body updatePasswordBody: UpdateLocationBody): Response<DefaultResponse>

    @PUT("/user/update-profile")
    suspend fun updateProfile(@Body updateProfileBody: UpdateProfileBody): Response<User>
}