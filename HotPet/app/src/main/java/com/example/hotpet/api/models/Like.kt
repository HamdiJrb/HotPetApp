package com.example.hotpet.api.models

import java.io.Serializable

data class Like(
    var _id: String?,
    var liked: User?,
    var liker: User?,
    var isRight: Boolean,
    var isMatch: Boolean,
) : Serializable
