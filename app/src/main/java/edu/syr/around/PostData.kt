package edu.syr.around

import java.io.Serializable

data class PostData(
    var uid: String, //key
    var title: String,
    var content: String,
    var userId: String,
    var imagePath: String,
    var postTime : String,
    var likers: String,
    var comments : CommentData?,
    var location : String
) : Serializable {
    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        null,
        ""
    )
}