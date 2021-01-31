package edu.syr.around

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.CameraUpdateFactory
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class PostMapFragment : Fragment(),
    GoogleMap.OnInfoWindowClickListener {
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    lateinit var googleMap: GoogleMap

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_map, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onStart() {
        var mapFragment : SupportMapFragment = childFragmentManager?.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(OnMapReadyCallback {
            googleMap = it
            googleMap.setOnInfoWindowClickListener(this)

            setUpMap()
        })
        mapFragment.getMapAsync { mMap ->
            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

            mMap.clear() //clear old markers

            val googlePlex = CameraPosition.builder()
                .target(MapUtils.getCurLocation())
                .zoom(16f)
                .bearing(0f)
                .tilt(45f)
                .build()

            for (post in PostUtils.items) {
                mMap.addMarker(
                    MarkerOptions()
                        .position(MapUtils.stringToLatLng(post.location))
                        .title(post.title)
                        .snippet(PostUtils.trimTime(post.postTime))
                )
            }

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 2000, null)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity as Activity)
        super.onStart()
    }

    private fun setUpMap() {
        googleMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(activity as Activity) { location ->
            if (location != null) {
                lastLocation = location
                MapUtils.currentLatitude = location.latitude
                MapUtils.currentLongitude = location.longitude
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(MapUtils.getCurLocation(), 12f))
            }
        }
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onInfoWindowClickFromPostMap(post : PostData)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PostMapFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
        fun newInstance() =
            PostMapFragment().apply {
                arguments = Bundle().apply {

                }
            }
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onInfoWindowClick(marker: Marker?) {
        Log.e("onInfoWindowCLick", "called")
        if (marker == null ){
            return
        }
        var p : PostData? = null
        val position = MapUtils.latLngToString(marker.position)
        for (item in PostUtils.items) {
            if (position == item.location) {
                p = item
            }
        }
        if (p == null) {
            return
        }
        Log.e("onInfoWindowCLick", "post is not null")
        listener!!.onInfoWindowClickFromPostMap(p!!)
    }
}
