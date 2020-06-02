package pl.piotrgorny.gymondo

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

@BindingAdapter("imageUrl")
fun ImageView.loadImageByUrl(url: String?) {
    if(url == null){
        return
    }
    Picasso.get()
        .load(url)
        .placeholder(R.drawable.ic_image)
        .error(R.drawable.ic_error)
        .into(this)
}