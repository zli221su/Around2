package edu.syr.around

import android.view.View
import android.widget.ImageView
import com.squareup.picasso.Picasso

fun setImageSource(view: View, imagePath: String?, imageView: Int) {
    if (imagePath == null) {
        return
    }
    val picasso = Picasso.Builder(view.context).listener{ _, _, e->e.printStackTrace() }.build()
    picasso.load(imagePath).into(view.findViewById<ImageView>(imageView))
}