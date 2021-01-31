package edu.syr.around

import java.io.Serializable

data class CommentData(
    var userUid: String,
    var content: String,
    var commentTime: String


) : Serializable {
    constructor() : this(
        "",
        "",
        ""
    )
}