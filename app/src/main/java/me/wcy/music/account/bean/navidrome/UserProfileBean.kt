package me.wcy.music.account.bean.navidrome


import com.google.gson.annotations.SerializedName

data class UserProfileBean(
    @SerializedName("createdAt")
    var createdAt: String = "",
    @SerializedName("email")
    var email: String = "",
    @SerializedName("id")
    var id: String = "",
    @SerializedName("isAdmin")
    var isAdmin: Boolean = false,
    @SerializedName("lastAccessAt")
    var lastAccessAt: String = "",
    @SerializedName("lastLoginAt")
    var lastLoginAt: String = "",
    @SerializedName("name")
    var name: String = "",
    @SerializedName("updatedAt")
    var updatedAt: String = "",
    @SerializedName("userName")
    var userName: String = ""
)