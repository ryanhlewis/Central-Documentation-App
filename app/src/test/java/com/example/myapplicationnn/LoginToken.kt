package com.example.myapplicationnn

class LoginToken {
    var access_token: String = ""
    var login : String = ""
    var avatar_url : String = ""
    fun getToken(): String {
        return access_token
    }
    fun getLog() : String {
        return login
    }
    fun getAvatar() : String {
        return avatar_url
    }

}
