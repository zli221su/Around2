package edu.syr.around

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError


object FirebaseUtils {

    var currentUser: UserData = UserData()
    var users = ArrayList<UserData>()

    fun getCurUser(): UserData {
        return currentUser
    }

    val usersRef = PostUtils.myDB.child("users")

    fun initFirebaseUtils() {
        currentUser.uid = FirebaseAuth.getInstance().uid!!
        usersRef.addChildEventListener(FirebaseUtils.userChildEventListener)
    }

    val userChildEventListener = object: ChildEventListener {
        override fun onCancelled(p0: DatabaseError) {
        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            val data = p0.getValue<UserData>(UserData::class.java)
            val key = p0.key
        }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            val data = p0.getValue<UserData>(UserData::class.java)
            val key = p0.key
            if (data != null && key != null) {
                if (FirebaseUtils.currentUser.uid.equals(key)) {
                    FirebaseUtils.currentUser = data
                }
                for ((index, user) in FirebaseUtils.users.withIndex()) {
                    if (user.uid.equals(key)) {
                        FirebaseUtils.users.removeAt(index)
                        FirebaseUtils.users.add(index, data)
                        break
                    }
                }
            }
        }

        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
            val data = p0.getValue<UserData>(UserData::class.java)
            val key = p0.key
            if (data != null && key != null) {
                if (FirebaseUtils.currentUser.uid.equals(key)) {
                    FirebaseUtils.currentUser = data
                }
                var insertPos = FirebaseUtils.users.size
                for (user in FirebaseUtils.users) {
                    if (user.uid.equals(key))
                        return
                }
                FirebaseUtils.users.add(insertPos, data)
            }
        }

        override fun onChildRemoved(p0: DataSnapshot) {
            val data = p0.getValue<UserData>(UserData::class.java)
            val key = p0.key
            if (data != null && key != null) {
                if (FirebaseUtils.currentUser.uid.equals(key)) {
                    FirebaseUtils.currentUser = UserData()
                }
                var delPos = -1
                for ((index, user) in FirebaseUtils.users.withIndex()) {
                    if (FirebaseUtils.users.equals(key)) {
                        delPos = index
                        break
                    }
                }
                if (delPos != -1) {
                    FirebaseUtils.users.removeAt(delPos)
                }
            }
        }
    }
}