package edu.syr.around

import android.location.LocationManager
import com.google.android.gms.maps.model.LatLng


object MapUtils {

    var currentLatitude : Double = 43.038735
    var currentLongitude : Double = -76.134519

    fun getCurLocation() : LatLng {
        return LatLng(currentLatitude, currentLongitude)
    }

    fun latLngToString(position : LatLng?) : String {
        if (position == null) {
            return ""
        }
        var positionString  = position.toString()
        var indexLeft = positionString.indexOf('(')
        var indexRight = positionString.indexOf(')')
        return positionString.substring(indexLeft, indexRight + 1)
    }

    fun stringToLatLng(s : String) : LatLng {
        var locationString = s
        var indexLeft = locationString.indexOf('(')
        var indexMid = locationString.indexOf(',')
        var indexRight = locationString.indexOf(')')

        var latitude : Double = locationString.substring(indexLeft + 1, indexMid).toDouble()
        var longitude : Double = locationString.substring(indexMid + 1, indexRight).toDouble()

        return LatLng(latitude, longitude)
    }

    private var locationManager : LocationManager? = null
}