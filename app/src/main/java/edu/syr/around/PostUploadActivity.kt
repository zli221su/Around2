package edu.syr.around

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_post_upload.*
import java.sql.Timestamp
import java.util.*

class PostUploadActivity : AppCompatActivity(),
    OnMapReadyCallback,
    GoogleMap.OnMapClickListener {

    var selectedPhotoUri: Uri? = null

    private var mapFragment: SupportMapFragment = SupportMapFragment.newInstance()
    private lateinit var googleMap: GoogleMap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_upload)

        btPostUploadLocation.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragMapLocationChosen, mapFragment)
                .addToBackStack("Position Chosen")
                .commit()
            mapFragment.getMapAsync { mMap ->
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

                mMap.clear() //clear old markers

                val googlePlex = CameraPosition.builder()
                    .target(MapUtils.getCurLocation())
                    .zoom(16f)
                    .bearing(0f)
                    .tilt(45f)
                    .build()
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 2000, null)
            }

        }
        btPostUploadPost.setOnClickListener {
            post()
        }
        btPostUploadCancel.setOnClickListener {
            val intent = Intent(applicationContext, PostActivity::class.java)
            startActivity(intent)
        }
        selectphoto_button.setOnClickListener {
            Log.d("SignUp"
                , "Try to show photo selector")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        mapFragment.getMapAsync(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
// proceed and check what the selected image was....
            selectedPhotoUri = data.data
            Log.e("Post", "Image was selected url = :$selectedPhotoUri")
            val bitmap = MediaStore.Images.Media.getBitmap(applicationContext!!.contentResolver, selectedPhotoUri)
            selectphoto_imageview.setImageBitmap(bitmap)
            selectphoto_button.alpha = 0f // hide button for selected photo imageview
        }
    }


    private fun post() {
        val title = editTxtPostUploadTitle.text.toString()
        val content = editTxtPostUploadContent.text.toString()
        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(applicationContext, "Please enter text in email/pw", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedPhotoUri == null){
            Toast.makeText(applicationContext, "You must select a photo for your post!", Toast.LENGTH_SHORT).show()
            return
        }
        val location : String = txtPostUploadLocation.text.toString()
        if (location == getString(R.string.location_not_selected)) {
            Toast.makeText(applicationContext, "You must choose the location for your post on map!", Toast.LENGTH_SHORT).show()
            return
        }

        var postData = PostData(
            UUID.randomUUID().toString(),
            title,
            content,
            FirebaseAuth.getInstance().uid ?: "",
            selectedPhotoUri.toString(),
            Timestamp(System.currentTimeMillis()).toString(),
            "",
            null,
            location
        )

        val filename = UUID.randomUUID().toString()
        val storageRef = FirebaseStorage.getInstance().getReference("/postimages/$filename")
        storageRef.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("SignUp"
                    , "Successfully uploaded image: ${it.metadata?.path}")
                storageRef.downloadUrl.addOnSuccessListener {
                    Log.d("SignUp"
                        , "File Location: $it")
                    postData.imagePath = it.toString()
                    savePostToFirebaseDatabase(postData)
                }
            }
            .addOnFailureListener {
                Log.d("SignUp"
                    , "Failed to upload image to storage: ${it.message}")
            }
    }

    private fun savePostToFirebaseDatabase(post: PostData) {
        val uid = post.uid
        val ref = FirebaseDatabase.getInstance().getReference("/posts/$uid")
        ref.setValue(post)
            .addOnSuccessListener {
// launch the Main activity, clear back stack,
// not going back to login activity with back press button
                val intent = Intent(applicationContext, PostActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d("Post Upload"
                    , "Failed to set value to database: ${it.message}")
            }
    }

    override fun onMapReady(map: GoogleMap?) {
        googleMap = map!!
        googleMap.setOnMapClickListener(this)
    }

    override fun onMapClick(position: LatLng?) {
        txtPostUploadLocation.text = MapUtils.latLngToString(position)
        supportFragmentManager.beginTransaction()
            .remove(mapFragment)
            .addToBackStack("Position Chosen Finish")
            .commit()
        Toast.makeText(this, "Post Location Chosen: " + txtPostUploadLocation.text, Toast.LENGTH_SHORT).show()
    }


}
