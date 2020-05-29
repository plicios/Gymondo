package pl.piotrgorny.gymondo

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

@BindingAdapter("imageUrl")
fun ImageView.loadImageByUrl(url: String) {
    Picasso.get()
        .load(url)
        .placeholder(R.drawable.ic_launcher_background) //TODO change placeholder drawable
        .error(R.drawable.ic_launcher_background)       //TODO change error drawable
        .into(this)
}