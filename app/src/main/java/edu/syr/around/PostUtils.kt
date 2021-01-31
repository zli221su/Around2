package edu.syr.around

import com.google.firebase.database.*

object PostUtils {
    val myDB : DatabaseReference = FirebaseDatabase.getInstance().reference
    val posts= myDB.child("posts")

    val items = ArrayList<PostData>()

    fun trimTime(time : String) : String {
        return time.substring(0, 19)
    }
}