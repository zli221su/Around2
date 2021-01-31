package edu.syr.around

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_post.*
import java.io.Serializable

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM = "param1"

class PostFragment : Fragment(),
    OnMapReadyCallback {
    private var post: PostData? = null

    private val myDB : DatabaseReference = FirebaseDatabase.getInstance().reference
    private val posts= myDB.child("posts")

    private var mapFragment: SupportMapFragment = SupportMapFragment.newInstance()
    private lateinit var googleMap: GoogleMap

    val userRef = FirebaseDatabase.getInstance().getReference("/users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            post = it.getSerializable(ARG_PARAM) as PostData
        }
    }

    override fun onStart() {
        super.onStart()

        cbxPostDetailLike.setOnCheckedChangeListener {buttonView, isChecked ->
            var curUser = FirebaseUtils.getCurUser()

            if (isChecked) {
                // add post uuid to curUser
                post!!.likers = LFHelper.addId(post!!.likers, curUser.uid)
                cbxPostDetailLike.text = "Unlike"
            }
            else {
                // remove post uuid from curUser's list
                post!!.likers = LFHelper.removeId(post!!.likers, curUser.uid)
                cbxPostDetailLike.text = "Like"
            }
            posts.child(post!!.uid).setValue(post)
        }

        cbxPostDetailFollow.setOnCheckedChangeListener {buttonView, isChecked ->
            var postUser : UserData? = null
            for (user in FirebaseUtils.users) {
                if (user.uid == post!!.userId) {
                    postUser = user
                    break
                }
            }
            var curUser = FirebaseUtils.getCurUser()

            if (isChecked) {
                // add post uuid to curUser
                postUser!!.followers = LFHelper.addId(postUser!!.followers, curUser.uid)
                cbxPostDetailFollow.text = "Unfollow"
            }
            else {
                // remove post uuid from curUser's list
                postUser!!.followers = LFHelper.removeId(postUser!!.followers, curUser.uid)
                cbxPostDetailFollow.text = "Follow"
            }
            userRef.child(postUser.uid).setValue(postUser)
        }

        var location = MapUtils.stringToLatLng(post!!.location)
        btPostDetailLocation.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.meContainer, mapFragment)
                .addToBackStack("Position Peek")
                .commit()

            mapFragment.getMapAsync { mMap ->
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

                mMap.clear() //clear old markers

                val googlePlex = CameraPosition.builder()
                    .target(location)
                    .zoom(16f)
                    .bearing(0f)
                    .tilt(45f)
                    .build()

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 2000, null)

                mMap.addMarker(
                    MarkerOptions()
                        .position(location)
                        .title("CIS 600")
                        .snippet("Android Programing.\n5:00 pm - 6:20 pm")
                )
            }
        }
    }

    override fun onMapReady(map: GoogleMap?) {
        googleMap = map!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setPostInfo(view)
    }

    private fun setPostInfo(view : View) {
        var postUser : UserData? = null
        for (user in FirebaseUtils.users) {
            if (user.uid == post!!.userId) {
                postUser = user
            }
        }

        txtPostDetailTitle.text = post?.title
        txtPostDetailContent.text = post?.content
        txtPostDetailAuthor.text = "Posted by: " + postUser!!.userName
        txtPostDetailTime.text = PostUtils.trimTime(post?.postTime!!)

        cbxPostDetailFollow.isChecked = LFHelper.isIdExist(postUser!!.followers, FirebaseUtils.getCurUser().uid)
        cbxPostDetailLike.isChecked = LFHelper.isIdExist(post!!.likers, FirebaseUtils.getCurUser().uid)

        val imageUrl = post?.imagePath!!
        Log.e("ImagePath", imageUrl)
        var imageView = view.findViewById<ImageView>(R.id.imgPostDetailImage)
        Picasso.get().load(imageUrl).fit().error(R.mipmap.ic_launcher).into(imageView)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: PostData) =
            PostFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM, param1 as Serializable)
                }
            }
    }
}
