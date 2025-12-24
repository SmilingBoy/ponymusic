package me.wcy.music.account.bean.navidrome

import com.google.gson.annotations.SerializedName

data class LoginResultBean(
    @SerializedName("id")
    var id: String = "",
    @SerializedName("isAdmin")
    var isAdmin: Boolean = false,
    @SerializedName("lastFMApiKey")
    var lastFMApiKey: String = "",
    @SerializedName("name")
    var name: String = "",
    @SerializedName("subsonicSalt")
    var subsonicSalt: String = "",
    @SerializedName("subsonicToken")
    var subsonicToken: String = "",
    @SerializedName("token")
    var token: String = "",
    @SerializedName("username")
    var username: String = "",


    @SerializedName("error")
    var error: String? = ""

)


