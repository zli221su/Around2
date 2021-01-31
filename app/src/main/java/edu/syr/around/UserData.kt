package edu.syr.around

import java.io.Serializable

data class UserData(
    var uid: String,
    var userName: String,
    var userEmail: String,
    var profileImageUrl: String,

    var age: Int,
    var phoneNumber: String,
    var description: String,

    var followers: String
) : Serializable {
    constructor() : this(
        "",
        "",
        "",
        "",
        0,
        "",
        "",
        ""
    )
}