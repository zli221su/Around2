package edu.syr.around

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.navigation.NavigationView
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.mikhaellopez.circularimageview.CircularImageView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.android.synthetic.main.fragment_self_profile.*

class PostActivity :
    AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    PostMapFragment.OnFragmentInteractionListener,
    PostListFragment.OnFragmentInteractionListener {

    private var curFragmentName : String? = null
    private var curFragment : Fragment? = null
    private var curPost : PostData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        setSupportActionBar(post_tool_bar)
        val toggle = ActionBarDrawerToggle(this, activity_post, post_tool_bar, R.string.open_nav, R.string.close_nav)
        activity_post.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)


        FirebaseUtils.initFirebaseUtils()


        fabHome.setOnClickListener {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.meContainer, PostMapFragment())
                .addToBackStack("post map")
                .commit()
        }

        // save UI states
        curFragmentName = savedInstanceState?.getString("current fragment name")
        curPost = savedInstanceState?.getSerializable("current post") as PostData?

        curFragment = when (curFragmentName) {
            "PostMapFragment" -> PostMapFragment()
            "PostListFragment" -> PostListFragment()
            "PostFragment" -> PostFragment.newInstance(curPost!!)
            else -> PostHomeFragment()
        }

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.meContainer, curFragment!!)
            .addToBackStack("post home")
            .commit()
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle?) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState?.putString("current fragment name", curFragmentName)
        savedInstanceState?.putSerializable("current post", curPost)
    }

    override fun onStart() {
        super.onStart()
        val headerView = navView.getHeaderView(0)
        val profileUid = headerView.findViewById<TextView>(R.id.navUserName)
        val profileEmail = headerView.findViewById<TextView>(R.id.navUserEmail)
        val profileImage = headerView.findViewById<CircularImageView>(R.id.navUserImage)
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val profileRef = FirebaseUtils.usersRef.child(firebaseUser!!.uid)
        profileRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot != null){
                    profileEmail.text = dataSnapshot.child("userEmail").value.toString()
                    profileUid.text = dataSnapshot.child("userName").value.toString()
                    var userImage : String = dataSnapshot.child("profileImageUrl").value.toString()
                    if (userImage != null) {
                        Picasso.get().load(userImage).fit().into(profileImage)
                    }
                    else {
                        profileImage.setImageResource(R.drawable.ic_error_black_24dp)
                    }
                }
            }
        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.nav_post_map -> {
                curFragmentName = "PostMapFragment"
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.meContainer, PostMapFragment())
                    .addToBackStack("post map")
                    .commit()
            }
            R.id.nav_post_list -> {
                curFragmentName = "PostListFragment"
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.meContainer, PostListFragment())
                    .addToBackStack("post list")
                    .commit()
            }
            R.id.nav_post_new -> {
                val intent = Intent(this, PostUploadActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_author -> {
                val intent = Intent(this, AuthorActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_logout -> {
                signOut()
            }
        }
        activity_post.closeDrawer(GravityCompat.START)
        return true
    }

    private fun signOut(){
        FirebaseAuth.getInstance().signOut()
// launch the Login activity
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onBackPressed() {
        if(activity_post.isDrawerOpen(GravityCompat.START)){
            activity_post.closeDrawer(GravityCompat.START)
        }
        else
            super.onBackPressed()
    }

    // handle click action in recycler view
    override fun onItemClicked(post: PostData) {
        curFragmentName = "PostFragment"
        curPost = post
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.meContainer, PostFragment.newInstance(post))
            .addToBackStack("post")
            .commit()
    }

    // handle click action in map view
    override fun onInfoWindowClickFromPostMap(post: PostData) {
        curFragmentName = "PostFragment"
        curPost = post
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.meContainer, PostFragment.newInstance(post))
            .addToBackStack("post")
            .commit()
    }
}
