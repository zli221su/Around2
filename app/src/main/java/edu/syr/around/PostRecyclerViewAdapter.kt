package edu.syr.around

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.ScaleAnimation
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class PostRecyclerViewAdapter(context: Context) :
    androidx.recyclerview.widget.RecyclerView.Adapter<PostRecyclerViewAdapter.PostViewHolder>() {

    var myListener: MyItemClickListener? = null
    var lastPosition = -1 // for animation
    val TAG = "FB Adapter"
    val items = PostUtils.items


//    private val myDB : DatabaseReference = FirebaseDatabase.getInstance().reference
    private val posts= PostUtils.posts

    val uid = FirebaseAuth.getInstance().uid ?: ""
    val userRef = FirebaseDatabase.getInstance().getReference("/users")


    var childEventListener = object : ChildEventListener {
        override fun onCancelled(p0: DatabaseError) {
            Log.d(ContentValues.TAG, "child event listener - onCancelled" + p0.toException())
        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            Log.d(ContentValues.TAG, "child event listener - onChildMoved$p0")
//            val data = p0.getValue<PostData>(PostData::class.java)
//            val key = p0.key
//            notifyDataSetChanged()
        }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            Log.d(ContentValues.TAG, "child event listener - onChildChanged$p0")
            val data = p0.getValue<PostData>(PostData::class.java)
            val key = p0.key
            if (data != null && key != null) {
                for ((index, post) in PostUtils.items.withIndex()) {
                    if (post.uid.equals(key)) {
                        PostUtils.items.removeAt(index)
                        PostUtils.items.add(index, data)
//                        notifyItemChanged(index)
//                        notifyDataSetChanged()
                        break
                    }
                }
            }
        }

        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
            Log.d(ContentValues.TAG, "child event listener - onChildAdded$p0")
            val data = p0.getValue<PostData>(PostData::class.java)
            val key = p0.key
            if (data != null && key != null) {
                var insertPos = PostUtils.items.size
                for (post in PostUtils.items) {
                    if (post.uid.equals(key))
                        return
                }
                myListener?.onNotifyPostAddedFromAdapter(data)
                PostUtils.items.add(insertPos, data)
                notifyItemInserted(insertPos)
//                notifyDataSetChanged()
            }
        }

        override fun onChildRemoved(p0: DataSnapshot) {
            Log.d(ContentValues.TAG, "child event listener - onChildRemoved$p0")
            val data = p0.getValue<PostData>(PostData::class.java)
            val key = p0.key
            if (data != null && key != null) {
                var delPos = -1
                for ((index, post) in PostUtils.items.withIndex()) {
                    if (post.uid.equals(key)) {
                        delPos = index
                        break
                    }
                }
                if (delPos != -1) {
                    PostUtils.items.removeAt(delPos)
                    notifyItemRemoved(delPos)
                }
            }
        }
    }

    init {
        PostUtils.posts.addChildEventListener(this.childEventListener)
    }

    // Adapter Listener!!!
    interface MyItemClickListener {
        fun onItemClickedFromAdapter(post: PostData)
        fun onNotifyPostAddedFromAdapter(post: PostData)
    }

    fun setMyItemClickListener(listener: MyItemClickListener) {
        this.myListener = listener
    }

    // MUST DO!!
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): PostViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context) // p0 is parent
        val view : View
// p1 -> View Type, check getItemViewType function!!
        view = layoutInflater.inflate(R.layout.post_rv_item, p0, false)
        return PostViewHolder(view)
    }
    // MUST DO!!
    override fun getItemCount(): Int {
        return items.size
    }
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = items[position]
        var postUser : UserData? = null
        Log.e("users.size = ", FirebaseUtils.users.size.toString())
        for (user in FirebaseUtils.users) {
            if (user.uid == post.userId) {
                postUser = user
            }
        }

        holder.postTitle.text = post.title
        holder.postLike.isChecked = LFHelper.isIdExist(post.likers, FirebaseUtils.getCurUser().uid)
        holder.postFollow.isChecked = LFHelper.isIdExist(postUser!!.followers, FirebaseUtils.getCurUser().uid)
        holder.postLikeAmount.text = LFHelper.getIdCount(post.likers).toString()

        val imageUrl = post.imagePath!!
        Picasso.get().load(imageUrl).fit().error(R.mipmap.ic_launcher).into(holder.postImage)
        holder.postImage

        var length = post.content!!.length
        length = if (length > 150) 150 else length
        holder.postContent.text = post.content.substring(0, length - 1) + " ..."

        setAnimation(holder.postImage, position)
    }



    private fun setAnimation(view: View, position: Int){
        if(position != lastPosition){
            when(getItemViewType(position)){
                1 -> {
                    val animation = AnimationUtils.loadAnimation(view.context, android.R.anim.slide_in_left)
                    animation.duration = 700
                    animation.startOffset = position * 100L
                    view.startAnimation(animation)
                }

                else -> {
                    val animation = ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f)
                    animation.duration = 500
                    animation.startOffset = position * 200L
                    view.startAnimation(animation)
                }
            }
//animation.startOffset = position * 100L
            lastPosition = position
        }
    }

    fun getItem(index: Int) : Any{
        return items[index]
    }
    fun findFirst(query: String?): Int{
        var pos = -1;
        for( i in items.indices ) {
            var title : String? = items[i].title
            title = title?.toLowerCase()
            var lowerQuery = query?.toLowerCase()
            if(title?.indexOf(lowerQuery!!) != -1){
                pos = i
                break;
            }
        }
        return pos;
    }

    inner class PostViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val postImage = view.findViewById<ImageView>(R.id.rvImage)
        val postTitle = view.findViewById<TextView>(R.id.rvTitle)
        val postContent = view.findViewById<TextView>(R.id.rvContent)
        val postLike = view.findViewById<CheckBox>(R.id.rvLike)
        val postFollow = view.findViewById<CheckBox>(R.id.rvFollow)
        val postLikeAmount = view.findViewById<TextView>(R.id.rvLikeAmount)
        val overflow = view.findViewById<ImageView>(R.id.rvImage)

        init {
            view.setOnClickListener {
                if (myListener != null) {
                    if (adapterPosition != androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                        myListener!!.onItemClickedFromAdapter(items[adapterPosition])
                    }
                }
            }
            postLike.setOnCheckedChangeListener {buttonView, isChecked ->
                var post = items[adapterPosition]
                var curUser = FirebaseUtils.getCurUser()

                if (isChecked) {
                    // add post uuid to curUser
                    post.likers = LFHelper.addId(post.likers, curUser.uid)
                }
                else {
                    // remove post uuid from curUser's list
                    post.likers = LFHelper.removeId(post.likers, curUser.uid)
                }
                posts.child(post.uid).setValue(post)
                postLikeAmount.text = LFHelper.getIdCount(post.likers).toString()
            }
            postFollow.setOnCheckedChangeListener {buttonView, isChecked ->
                var post = items[adapterPosition]
                var postUser : UserData? = null
                for (user in FirebaseUtils.users) {
                    if (user.uid == post.userId) {
                        postUser = user
                        break
                    }
                }
                var curUser = FirebaseUtils.getCurUser()

                if (isChecked) {
                    // add post uuid to curUser
                    postUser!!.followers = LFHelper.addId(postUser!!.followers, curUser.uid)
                }
                else {
                    // remove post uuid from curUser's list
                    postUser!!.followers = LFHelper.removeId(postUser!!.followers, curUser.uid)
                }
                userRef.child(postUser!!.uid).setValue(postUser)
            }

        }
    }
}

